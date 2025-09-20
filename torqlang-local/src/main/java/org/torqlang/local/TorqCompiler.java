/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.klvm.ActorCfg;
import org.torqlang.klvm.ActorCtor;
import org.torqlang.klvm.ApplyInstr;
import org.torqlang.klvm.BindIdentToIdentInstr;
import org.torqlang.klvm.CommonFeatures;
import org.torqlang.klvm.Complete;
import org.torqlang.klvm.CompleteOrIdent;
import org.torqlang.klvm.CompleteRec;
import org.torqlang.klvm.CompleteRecBuilder;
import org.torqlang.klvm.Env;
import org.torqlang.klvm.EnvEntry;
import org.torqlang.klvm.FailedValue;
import org.torqlang.klvm.Ident;
import org.torqlang.klvm.Instr;
import org.torqlang.klvm.KernelModule;
import org.torqlang.klvm.LocalInstr;
import org.torqlang.klvm.Rec;
import org.torqlang.klvm.SeqInstr;
import org.torqlang.klvm.Str;
import org.torqlang.klvm.Var;
import org.torqlang.lang.ActorStmt;
import org.torqlang.lang.AnyType;
import org.torqlang.lang.AskStmt;
import org.torqlang.lang.BoolAsExpr;
import org.torqlang.lang.FeaturePat;
import org.torqlang.lang.FieldPat;
import org.torqlang.lang.Generator;
import org.torqlang.lang.HandleStmt;
import org.torqlang.lang.IdentAsExpr;
import org.torqlang.lang.IdentAsPat;
import org.torqlang.lang.ImportName;
import org.torqlang.lang.ImportStmt;
import org.torqlang.lang.Lang;
import org.torqlang.lang.LangConsumer;
import org.torqlang.lang.MetaFeature;
import org.torqlang.lang.MetaField;
import org.torqlang.lang.MetaRec;
import org.torqlang.lang.ModuleStmt;
import org.torqlang.lang.OpaqueProtocolType;
import org.torqlang.lang.OpaqueType;
import org.torqlang.lang.PackageStmt;
import org.torqlang.lang.Parser;
import org.torqlang.lang.Pat;
import org.torqlang.lang.ProtocolStmt;
import org.torqlang.lang.RecPat;
import org.torqlang.lang.RecType;
import org.torqlang.lang.ScalarType;
import org.torqlang.lang.Stmt;
import org.torqlang.lang.StrAsExpr;
import org.torqlang.lang.StrAsPat;
import org.torqlang.lang.StrType;
import org.torqlang.lang.TellStmt;
import org.torqlang.lang.TuplePat;
import org.torqlang.lang.TupleType;
import org.torqlang.lang.TupleTypeExpr;
import org.torqlang.lang.Type;
import org.torqlang.lang.TypeStmt;
import org.torqlang.util.FileName;
import org.torqlang.util.FileType;
import org.torqlang.util.ListTools;
import org.torqlang.util.Message;
import org.torqlang.util.MessageLevel;
import org.torqlang.util.NeedsImpl;
import org.torqlang.util.SourceFileBroker;
import org.torqlang.util.SourceSpan;
import org.torqlang.util.SourceString;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.torqlang.klvm.CommonFeatures.$NEW;
import static org.torqlang.klvm.Ident.$MAIN;
import static org.torqlang.util.ListTools.nullSafeCopyOf;

/*
 * Torq Module Terminology
 *
 *     Workspace -- a set of root folders where each root is a tree arranged to reflect its packages.
 *         Folder -- a filesystem folder with a tree structure arranged to reflect its packages.
 *             Package -- a branch within a folder tree that contains files.
 *                 Module -- a file within a folder branch that contains members.
 *                     Member -- an Actor, Func, Proc, Type, or Literal.
 *
 *     PackageRec -- used to avoid name clashes with the reserved Java word "package"
 *
 *     Bundle -- used as a verb to create a Package or PackageRec
 *
 * Type Records
 *
 *     Type names are materialized as type records with the following features:
 *         $new -- contains a constructor for the corresponding implementation if present. Using the $new
 *             constructor for an actor produces an ActorCfg instance. Using the $new constructor for a composite
 *             produces the expected Array, Rec, Tuple, or Obj.
 *         $type -- contains an opaque wrapper around the parsed type definition.
 *         Other features -- contains the methods defined within the `static {...}` block or by the native
 *             implementation.
 *
 * Exported Values
 *
 *     Method (Func or Proc) -- the definition is exported as a value. The method name is a type name.
 *
 *     Actor, Type, or Protocol -- the definition is exported as a type record.
 *
 * Torq Packages and Bundling
 *
 *     The Torq compiler is used to collect and bundle artifacts into packages defined in the workspace. A physical
 *     package is a Torq record where each feature is a Name and each value is the exported value. Physical packages
 *     are collected as a map where the key is the package qualifier and the value is the physical package record.
 *
 * Stages:
 *
 *     Parse: Visit each Torq file in the workspace and parse it
 *         Parse each Torq file into a Module structure
 *             Each Module structure contains a ModuleStmt
 *
 *     Collect: Visit each import and export in each Module
 *         Create a Member structure for each qualified name found
 *             Put imports and exports under their qualified name
 *             Note that each Member will name the other members that import it
 *                 A member that has imports but is not exported is likely a missing system component
 *                 A member that is exported but not imported is likely an unused system component
 *
 *     Generate: Visit each parsed module and generate its kernel instructions
 *         TODO: Create a global type environment to be used by generate
 *             This is a collection of packages containing types for type checking
 *         Set kernel instructions on each Module
 *
 *     Bundle: Bundle exported members by qualifier
 *         Gather exported members for each qualifier
 *         Gather exported API handlers for each qualifier
 *         Build a package record of exported members for each qualifier
 *
 */
public class TorqCompiler implements PackageProvider, TorqCompilerReady, TorqCompilerParsed, TorqCompilerGenerated, TorqCompilerCollected, TorqCompilerBundled {

    private static final String DOT_TORQ = ".torq";

    private final Map<String, CompiledModule> modulesByAbsolutePath = new HashMap<>();
    private final Map<String, CompiledMember> membersByQualifiedName = new HashMap<>();
    private final Map<String, CompiledPackage> packagesByQualifier = new HashMap<>();
    private final Map<String, CompiledApiHandler> apiHandlersByQualifiedName = new HashMap<>();
    private final List<Message> messages = new ArrayList<>();

    private ActorSystem rootActorSystem;
    private MessageLevel loggingLevel = MessageLevel.INFO;
    private State state = State.READY;
    private List<SourceFileBroker> workspace;

    private TorqCompiler() {
        // TODO: Make more fluent like we did with ActorBuilder
    }

    public static TorqCompilerReady create() {
        return new TorqCompiler();
    }

    /*
     * Return the name of a qualified name. For example return "HashMap" from "torq.util.HashMap".
     * Throw an IllegalArgumentException if the name is not qualified.
     */
    static String nameFrom(String qualifiedName) {
        int lastDot = qualifiedName.lastIndexOf('.');
        if (lastDot != -1) {
            return qualifiedName.substring(lastDot + 1);
        } else {
            throw new IllegalArgumentException("No qualifier");
        }
    }

    /*
     * Return the qualifier of a qualified name. For example return "torq.util" from "torq.util.HashMap".
     * Throw an IllegalArgumentException if the name is not qualified.
     */
    static String qualifierFrom(String qualifiedName) {
        int lastDot = qualifiedName.lastIndexOf('.');
        if (lastDot != -1) {
            return qualifiedName.substring(0, lastDot);
        } else {
            throw new IllegalArgumentException("No qualifier");
        }
    }

    @Override
    public Map<String, CompiledApiHandler> apiHandlers() {
        return apiHandlersByQualifiedName;
    }

    private void addApiRoute(HandleStmt handleStmt, CompiledApiHandler apiHandler) {
        addTraceMessage("Adding API route for: " + handleStmt.pat);
        TuplePat pathTuplePat = getPathTuplePat(handleStmt.pat);
        String pathExpr = getPathExpr(pathTuplePat);
        if (!apiHandler.routes().stream().filter(r -> r.pathExpr().equals(pathExpr)).toList().isEmpty()) {
            throw new IllegalStateException("Duplicate path expression: " + pathExpr);
        }
        CompiledApiRoute route = new CompiledApiRoute(pathExpr);
        route.setPathType(getPathType(pathTuplePat));
        route.setQueryType(RecType.SINGLETON);
        route.setInputType(AnyType.SINGLETON);
        route.setOutputType(AnyType.SINGLETON);
        apiHandler.routes().add(route);
    }

    private void addInfoMessage(String message) {
        if (MessageLevel.INFO.ordinal() <= loggingLevel.ordinal()) {
            messages.add(Message.create("TorqCompilerTrace", MessageLevel.INFO, message));
        }
    }

    private void addTraceMessage(String message) {
        if (MessageLevel.TRACE.ordinal() <= loggingLevel.ordinal()) {
            messages.add(Message.create("TorqCompilerTrace", MessageLevel.TRACE, message));
        }
    }

    private void addWarnMessage(String message) {
        if (MessageLevel.WARN.ordinal() <= loggingLevel.ordinal()) {
            messages.add(Message.create("TorqCompilerWarn", MessageLevel.WARN, message));
        }
    }

    @Override
    public final TorqCompilerBundled bundle() {
        if (state != State.GENERATED) {
            throw new TorqCompilerError("Cannot bundle at state: " + state, messages);
        }
        addInfoMessage("Grouping exported members by qualifier");
        for (CompiledMember member : membersByQualifiedName.values()) {
            CompiledPackage packageValue = packagesByQualifier.computeIfAbsent(member.qualifier(), CompiledPackage::new);
            addTraceMessage("Adding exported member to its package group: " + member.qualifiedName());
            packageValue.members().add(member);
            if (isApiHandler(member.export())) {
                addTraceMessage("Marking exported member as an API handler: " + member.qualifiedName());
                member.setApiHandler(true);
            }
        }
        addInfoMessage("Done grouping exported members by qualifier");
        addInfoMessage("Packaging exported members for each qualifier");
        for (CompiledPackage packageValue : packagesByQualifier.values()) {
            // Bundle non-API exports
            bundlePackage(packageValue);
        }
        addInfoMessage("Done packaging exported members for each qualifier");
        addInfoMessage("Creating root actor system");
        ActorSystemBuilder builder = ActorSystem.builder();
        for (CompiledPackage packageValue : packagesByQualifier.values()) {
            addTraceMessage("Adding package to root system: " + packageValue.qualifier());
            builder.addPackage(packageValue.qualifier(), packageValue.packageRec());
        }
        rootActorSystem = builder.build();
        addInfoMessage("Done creating root actor system");
        addInfoMessage("Bundling API Handlers");
        for (CompiledPackage packageValue : packagesByQualifier.values()) {
            // Bundle API exports
            bundleApiHandlers(packageValue);
        }
        addInfoMessage("Done bundling API Handlers");
        this.state = State.BUNDLED;
        return this;
    }

    private void bundleApiHandlers(CompiledPackage packageValue) {
        for (CompiledMember member : packageValue.members()) {
            if (member.isApiHandler()) {
                addTraceMessage("Creating API handler for: " + member.qualifiedName());
                CompiledApiHandler apiHandler = apiHandlersByQualifiedName.computeIfAbsent(member.qualifiedName(), CompiledApiHandler::new);
                apiHandler.setMember(member);
                if (!(member.export() instanceof CompiledActorExport actorExport)) {
                    throw new IllegalArgumentException("Expected an actor export: " + member.export());
                }
                for (TellStmt tellStmt : actorExport.actorStmt().tellHandlers()) {
                    if (isExported(tellStmt)) {
                        addApiRoute(tellStmt, apiHandler);
                    }
                }
                for (AskStmt askStmt : actorExport.actorStmt().askHandlers()) {
                    if (isExported(askStmt)) {
                        addApiRoute(askStmt, apiHandler);
                    }
                }
                enhanceModuleInstr(member);
                apiHandler.setActorImage(createActorImage(member));
                addTraceMessage("Done creating API handler for: " + member.qualifiedName());
            }
        }
    }

    private void bundlePackage(CompiledPackage packageValue) {
        CompleteRecBuilder builder = Rec.completeRecBuilder();
        for (CompiledMember member : packageValue.members()) {
            Complete value;
            // API handlers are bundled elsewhere
            if (!member.isApiHandler()) {
                // Exporting a user-defined composite type:
                //    1) The export value is a type record containing $new, $type, and static fields
                //    2) The $type field is derived from the actor, protocol, or type expression
                //    3) The $new field is derived from the Actor, Protocol, or Type constructor
                //    4) The export can reference a native module that contains $new, $type, and static fields
                // Exporting a method:
                //    1) The export value is the generated function or procedure
                if (member.export() instanceof CompiledActorExport actorExport) {
                    value = makeExportValue(actorExport);
                } else if (member.export() instanceof CompiledTypeExport typeExport) {
                    value = makeExportValue(typeExport);
                } else if (member.export() instanceof CompiledProtocolExport protocolExport) {
                    value = makeExportValue(protocolExport);
                } else if (member.export() instanceof CompiledMethodExport methodExport) {
                    value = makeExportValue(methodExport);
                } else {
                    throw new IllegalArgumentException("Invalid export type: " + member.export());
                }
                builder.addField(Str.of(member.name()), value);
            }
        }
        packageValue.setPackageRec(builder.build());
    }

    @Override
    public final TorqCompilerCollected collect() throws Exception {
        if (state != State.PARSED) {
            throw new TorqCompilerError("Cannot collect at state: " + state, messages);
        }
        addInfoMessage("Collecting imports and exports from each parsed module");
        for (CompiledModule module : modulesByAbsolutePath.values()) {
            String qualifier = formatFileNamesAsPath(module.absolutePath());
            addTraceMessage("Collecting from " + qualifier);
            LangConsumer.consume(module.moduleStmt(), lang -> {
                if (lang instanceof ImportStmt importStmt) {
                    collectImports(importStmt, module);
                }
                FindExportResult langExport = findExport(lang);
                if (langExport != null) {
                    collectExport(langExport, module);
                }
            });
        }
        addInfoMessage("Done collecting imports and exports from each parsed module");
        addInfoMessage("Validating import and export references");
        addTraceMessage("Validating each module has an export");
        /*
         * The moving parts in this scenario are not obvious. Multiple file brokers can contribute to a module and
         * multiple modules can comprise a package. Therefore, we must iterate all modules by absolute path verifying
         * that an export exists somewhere for the package.
         */
        for (CompiledModule module : modulesByAbsolutePath.values()) {
            boolean found = false;
            for (CompiledMember member : membersByQualifiedName.values()) {
                if (member.qualifier().equals(module.qualifier()) && member.export() != null) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new TorqCompilerError("Module has no exports: " + module.qualifiedName(), messages);
            }
        }

        /*
         * An import must have an export
         */
        addTraceMessage("Validating imports correspond to an export");
        for (CompiledMember member : membersByQualifiedName.values()) {
            if (!member.whereUsed().isEmpty() && member.export() == null) {
                throw new TorqCompilerError("Member used as import is not exported: " + member.qualifiedName(), messages);
            }
        }
        /*
         * TODO: Validate native module references
         *       A Torq type should have all its members present in the native module
         */
        addTraceMessage("---TODO--- Validate Torq types against their native module references");
        addInfoMessage("Done validating imports correspond to an export");
        this.state = State.COLLECTED;
        return this;
    }

    /*
     * We call this method when an export was found in a parsed module, and we want to save that export for further
     * processing.
     */
    private void collectExport(FindExportResult exportResult, CompiledModule module) {
        String formattedQualifier = formatFileNamesAsQualifier(module.torqPackage());
        CompiledExport langExport;
        if (exportResult.lang instanceof ActorStmt actorStmt) {
            langExport = new CompiledActorExport(actorStmt, exportResult.stereotype, module);
        } else if (exportResult.lang instanceof TypeStmt typeStmt) {
            langExport = new CompiledTypeExport(typeStmt, module);
        } else if (exportResult.lang instanceof ProtocolStmt protocolStmt) {
            langExport = new CompiledProtocolExport(protocolStmt, module);
        } else {
            if (!(exportResult.lang instanceof HandleStmt)) {
                throw new TorqCompilerError("Invalid export: " + exportResult.lang, messages);
            }
            // At this time, we only process actor exports and not their handlers. Later, we will process handlers when
            // we make a pass to create API routers.
            langExport = null;
        }
        if (langExport != null) {
            String qualifiedName = formattedQualifier + "." + langExport.simpleName();
            addTraceMessage("Collecting export: " + qualifiedName);
            CompiledMember member = membersByQualifiedName.get(qualifiedName);
            if (member == null) {
                member = new CompiledMember(qualifiedName);
                member.setModule(module);
                membersByQualifiedName.put(qualifiedName, member);
            } else {
                if (member.export() != null) {
                    throw new TorqCompilerError("Duplicate export name: " + qualifiedName, messages);
                }
            }
            member.setExport(langExport);
        }
    }

    /*
     * Collect the given import that was found in the given module.
     */
    private void collectImports(ImportStmt importStmt, CompiledModule module) {
        String formattedQualifier = formatIdentsAsQualifier(importStmt.qualifier);
        for (ImportName in : importStmt.names) {
            String qualifiedName = formattedQualifier + "." + in.name.ident.name;
            addTraceMessage("Collecting import: " + qualifiedName);
            CompiledMember member = membersByQualifiedName.computeIfAbsent(qualifiedName, CompiledMember::new);
            if (member.whereUsed().contains(module)) {
                throw new TorqCompilerError("Duplicate import name: " + qualifiedName, messages);
            }
            member.whereUsed().add(module);
        }
    }

    private ActorImage createActorImage(CompiledMember member) {
        Env env = Env.create(LocalActor.rootEnv(), new EnvEntry($MAIN, new Var()));
        ComputeTools.computeInstr(this, member.module().moduleInstrEnhanced(), env);
        Rec actorRec;
        try {
            actorRec = (Rec) env.get($MAIN).resolveValue();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        ActorCtor actorCtor = (ActorCtor) actorRec.findValue($NEW);
        env = Env.create(LocalActor.rootEnv(),
            List.of(
                new EnvEntry(Ident.$ACTOR_CTOR, new Var(actorCtor)),
                new EnvEntry(Ident.$R, new Var())
            )
        );
        List<CompleteOrIdent> argsWithTarget = ListTools.append(CompleteOrIdent.class, List.of(), Ident.$R);
        List<Instr> localInstrs = new ArrayList<>();
        localInstrs.add(new ApplyInstr(Ident.$ACTOR_CTOR, argsWithTarget, SourceSpan.emptySourceSpan()));
        SeqInstr seqInstr = new SeqInstr(localInstrs, SourceSpan.emptySourceSpan());
        ComputeTools.computeInstr(this, seqInstr, env);
        ActorCfg actorCfg;
        try {
            actorCfg = (ActorCfg) env.get(Ident.$R).resolveValue();
        } catch (Exception exc) {
            throw new TorqCompilerError("Configuration error", messages, exc);
        }
        Address address = Address.create(member.qualifiedName());
        LocalActor localActor = new LocalActor(address, rootActorSystem);
        localActor.configure(actorCfg);
        Object response;
        try {
            response = RequestClient.builder()
                .setAddress(address)
                .send(localActor, CaptureImage.SINGLETON)
                .awaitResponse(10, TimeUnit.MILLISECONDS);
            if (response instanceof FailedValue failedValue) {
                throw new TorqCompilerError(failedValue.toString(), messages);
            }
        } catch (Exception exc) {
            throw new TorqCompilerError("Capture image error", messages, exc);
        }
        return (ActorImage) response;
    }

    private void enhanceModuleInstr(CompiledMember member) {
        Instr moduleInstrCandidate = member.module().moduleInstr();
        if (!(moduleInstrCandidate instanceof LocalInstr moduleInstr)) {
            throw new TorqCompilerError("Invalid module: " + member.module().qualifier(), messages);
        }
        Instr bodyInstr = moduleInstr.body;
        List<Instr> enhancedBodyInstrs = new ArrayList<>();
        // Inject a bind instruction so that we can retrieve the actor constructor
        Ident actorIdent = Ident.create(member.name());
        enhancedBodyInstrs.add(new BindIdentToIdentInstr($MAIN, actorIdent, bodyInstr.toSourceBegin()));
        if (bodyInstr instanceof SeqInstr seqInstr) {
            enhancedBodyInstrs.addAll(seqInstr.list);
        } else {
            enhancedBodyInstrs.add(bodyInstr);
        }
        if (member.module().moduleInstrEnhanced() != null) {
            throw new IllegalArgumentException("Module instruction already enhanced: " + member.module().qualifier());
        }
        member.module().setModuleInstrEnhanced(
            new LocalInstr(
                moduleInstr.xs,
                new SeqInstr(enhancedBodyInstrs, bodyInstr),
                moduleInstr.sourceSpan
            )
        );
    }

    private boolean equalsPackageStmt(List<FileName> torqPackage, PackageStmt packageStmt) {
        if (torqPackage.size() != packageStmt.path.size()) {
            return false;
        }
        for (int i = 0; i < torqPackage.size(); i++) {
            FileName left = torqPackage.get(i);
            IdentAsExpr right = packageStmt.path.get(i);
            if (!left.value().equals(right.ident.name)) {
                return false;
            }
        }
        return true;
    }

    private KernelModule fetchKernelModule(String qualifiedName) {
        Class<?> kernelModuleClass;
        try {
            kernelModuleClass = Class.forName(qualifiedName);
        } catch (ClassNotFoundException exc) {
            throw new IllegalArgumentException("Kernel module not found: " + qualifiedName, exc);
        }
        Method javaMethod;
        try {
            javaMethod = kernelModuleClass.getMethod("singleton");
        } catch (NoSuchMethodException exc) {
            throw new IllegalArgumentException("Kernel module does not have a singleton method: " + qualifiedName, exc);
        }
        Object singleton;
        try {
            singleton = javaMethod.invoke(null);
        } catch (Exception exc) {
            throw new IllegalStateException("Error invoking singleton method: " + javaMethod, exc);
        }
        if (!(singleton instanceof KernelModule kernelModule)) {
            throw new IllegalArgumentException("Singleton is not a kernel module: " + singleton);
        }
        return kernelModule;
    }

    private FindExportResult findExport(Lang lang) {
        if (lang.metaStruct() == null) {
            return null;
        }
        if (!(lang.metaStruct() instanceof MetaRec metaRec)) {
            throw new TorqCompilerError("Invalid meta structure: " + lang.metaStruct(), messages);
        }
        // meta#{'export': Bool, 'native': Str, 'stereotype': Str}
        boolean export = false;
        String nativeModule = null;
        String stereotype = null;
        for (MetaField metaField : metaRec.fields()) {
            if (isExportMetaFeature(metaField.feature)) {
                export = getExportMetaValue(metaField);
            } else if (isNativeMetaFeature(metaField.feature)) {
                if (metaField.value instanceof StrAsExpr nativeValue) {
                    nativeModule = nativeValue.str.value;
                } else {
                    throw new TorqCompilerError("Invalid native field: " + metaField, messages);
                }
            } else if (isStereotypeMetaFeature(metaField.feature)) {
                if (metaField.value instanceof StrAsExpr stereotypeValue) {
                    stereotype = stereotypeValue.str.value;
                } else {
                    throw new TorqCompilerError("Invalid stereotype field: " + metaField, messages);
                }
            }
        }
        if (export) {
            return new FindExportResult(lang, stereotype, nativeModule);
        } else {
            return null;
        }
    }

    private String formatFileNamesAsPath(List<FileName> path) {
        return path.stream().map(FileName::value).collect(Collectors.joining("/"));
    }

    private String formatFileNamesAsQualifier(List<FileName> path) {
        return path.stream().map(FileName::value).collect(Collectors.joining("."));
    }

    private String formatIdentsAsQualifier(List<IdentAsExpr> path) {
        return path.stream().map(id -> id.ident.name).collect(Collectors.joining("."));
    }

    @Override
    public final TorqCompilerGenerated generate() throws Exception {
        if (state != State.COLLECTED) {
            throw new TorqCompilerError("Cannot generate at state: " + state, messages);
        }
        addInfoMessage("Generating module instructions");
        for (Map.Entry<String, CompiledModule> moduleEntry : modulesByAbsolutePath.entrySet()) {
            addTraceMessage("Generating " + moduleEntry.getKey());
            Generator g = new Generator();
            moduleEntry.getValue().setModuleInstr(g.acceptStmt(moduleEntry.getValue().moduleStmt()));
        }
        addInfoMessage("Done generating module instructions");
        this.state = State.GENERATED;
        return this;
    }

    private boolean getExportMetaValue(MetaField metaField) {
        if (metaField.value instanceof BoolAsExpr boolAsExpr) {
            return boolAsExpr.bool.value;
        } else {
            throw new TorqCompilerError("Invalid export field: " + metaField, messages);
        }
    }

    private String getPathExpr(TuplePat pathTuplePat) {
        StringBuilder pathExprBuf = new StringBuilder();
        for (Pat elemPat : pathTuplePat.values()) {
            pathExprBuf.append("/");
            if (elemPat instanceof StrAsPat strAsPat) {
                pathExprBuf.append(strAsPat.str.value);
            } else if (elemPat instanceof IdentAsPat identAsPat) {
                pathExprBuf.append("{");
                pathExprBuf.append(identAsPat.ident.name);
                pathExprBuf.append("}");
            } else {
                throw new TorqCompilerError("Invalid API pattern: " + elemPat, messages);
            }
        }
        return pathExprBuf.toString();
    }

    private TuplePat getPathTuplePat(Pat pat) {
        Pat pathPat = null;
        if (!(pat instanceof RecPat recPat)) {
            throw new TorqCompilerError("Invalid API pattern: " + pat, messages);
        }
        for (FieldPat fieldPat : recPat.fields()) {
            if (isPathFeaturePat(fieldPat.feature)) {
                pathPat = fieldPat.value;
            }
        }
        if (pathPat == null) {
            throw new TorqCompilerError("Invalid API pattern: " + pat, messages);
        }
        if (!(pathPat instanceof TuplePat pathTuplePat)) {
            throw new TorqCompilerError("Invalid API pattern: " + pat, messages);
        }
        return pathTuplePat;
    }

    private TupleType getPathType(TuplePat pathTuplePat) {
        List<Type> pathElems = new ArrayList<>();
        for (Pat elemPat : pathTuplePat.values()) {
            if (elemPat instanceof StrAsPat) {
                pathElems.add(StrType.SINGLETON);
            } else if (elemPat instanceof IdentAsPat identAsPat) {
                if (identAsPat.type instanceof ScalarType scalarType) {
                    pathElems.add(scalarType);
                } else {
                    throw new TorqCompilerError("Invalid path element: " + elemPat, messages);
                }
            } else {
                throw new TorqCompilerError("Invalid API pattern: " + elemPat, messages);
            }
        }
        return TupleTypeExpr.createWithValues(pathElems);
    }

    private boolean isApiHandler(CompiledExport export) {
        if (!(export instanceof CompiledActorExport actorExport)) {
            return false;
        }
        return actorExport.stereotype() != null && actorExport.stereotype().equals("api");
    }

    private boolean isExported(Stmt stmt) {
        if (stmt.metaStruct() == null) {
            return false;
        }
        if (!(stmt.metaStruct() instanceof MetaRec metaRec)) {
            return false;
        }
        for (MetaField metaField : metaRec.fields()) {
            if (isExportMetaFeature(metaField.feature)) {
                return getExportMetaValue(metaField);
            }
        }
        return false;
    }

    private boolean isExportMetaFeature(MetaFeature metaFeature) {
        return (metaFeature instanceof StrAsExpr strAsExpr) && (strAsExpr.str.value.equals("export"));
    }

    private boolean isNativeMetaFeature(MetaFeature metaFeature) {
        return (metaFeature instanceof StrAsExpr strAsExpr) && (strAsExpr.str.value.equals("native"));
    }

    private boolean isPathFeaturePat(FeaturePat featurePat) {
        return (featurePat instanceof StrAsPat strAsPat) && (strAsPat.str.value.equals("path"));
    }

    private boolean isStereotypeMetaFeature(MetaFeature metaFeature) {
        return (metaFeature instanceof StrAsExpr strAsExpr) && (strAsExpr.str.value.equals("stereotype"));
    }

    private boolean isTorqSourceFile(String name) {
        return name.endsWith(DOT_TORQ);
    }

    private CompleteRec makeExportValue(CompiledActorExport actorExport) {
        // Exporting a user-defined composite type:
        //    1) The export value is a type record containing $new, $type, and static fields
        //    2) The $type field is derived from the actor, protocol, or type expression
        //    3) The $new field is derived from the Actor, Protocol, or Type constructor
        //    4) The export can reference a native module that contains $new, $type, and static fields
        FindExportResult findExportResult = findExport(actorExport.actorStmt());
        ActorStmt actorStmt = actorExport.actorStmt();
        if (findExportResult == null) {
            throw new IllegalArgumentException("Export not found: " + actorStmt);
        }
        if (findExportResult.stereotype != null) {
            throw new IllegalArgumentException("API handlers are exported elsewhere: " + actorStmt);
        }
        if (findExportResult.nativeModule != null) {
            return fetchKernelModule(findExportResult.nativeModule).namesake();
        }
        // TODO: We need to bundle the constructor under $new
        //       We need to bundle the static methods
        throw new NeedsImpl();
    }

    private CompleteRec makeExportValue(CompiledMethodExport methodExport) {
        throw new NeedsImpl();
    }

    private CompleteRec makeExportValue(CompiledProtocolExport protocolExport) {
        // Exporting a user-defined composite type:
        //    1) The export value is a type record containing $new, $type, and static fields
        //    2) The $type field is derived from the actor, protocol, or type expression
        //    3) The $new field is derived from the Actor, Protocol, or Type constructor
        //    4) The export can reference a native module that contains $new, $type, and static fields
        FindExportResult findExportResult = findExport(protocolExport.protocolStmt());
        ProtocolStmt protocolStmt = protocolExport.protocolStmt();
        if (findExportResult == null) {
            throw new IllegalArgumentException("Export not found: " + protocolStmt);
        }
        if (findExportResult.stereotype != null) {
            throw new IllegalArgumentException("Stereotype is not valid for a prototype: " + protocolStmt);
        }
        if (findExportResult.nativeModule != null) {
            return fetchKernelModule(findExportResult.nativeModule).namesake();
        }
        // TODO: We need to bundle the constructor under $new
        //       We need to bundle the static methods
        return Rec.completeRecBuilder()
            .addField(CommonFeatures.$TYPE, OpaqueProtocolType.create(protocolExport.protocolStmt()))
            .build();
    }

    private CompleteRec makeExportValue(CompiledTypeExport typeExport) {
        // Exporting a user-defined composite type:
        //    1) The export value is a type record containing $new, $type, and static fields
        //    2) The $type field is derived from the actor, protocol, or type expression
        //    3) The $new field is derived from the Actor, Protocol, or Type constructor
        //    4) The export can reference a native module that contains $new, $type, and static fields
        FindExportResult findExportResult = findExport(typeExport.typeStmt());
        TypeStmt typeStmt = typeExport.typeStmt();
        if (findExportResult == null) {
            throw new IllegalArgumentException("Export not found: " + typeStmt);
        }
        if (findExportResult.stereotype != null) {
            throw new IllegalArgumentException("Stereotype is not valid for a type: " + typeStmt);
        }
        if (findExportResult.nativeModule != null) {
            return fetchKernelModule(findExportResult.nativeModule).namesake();
        }
        // TODO: We need to bundle the constructor under $new
        //       We need to bundle the static methods
        return Rec.completeRecBuilder()
            .addField(CommonFeatures.$TYPE, OpaqueType.create(typeExport.typeStmt()))
            .build();
    }

    @Override
    public Map<String, CompiledMember> members() {
        return membersByQualifiedName;
    }

    @Override
    public final List<Message> messages() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public Map<String, CompiledModule> modules() {
        return modulesByAbsolutePath;
    }

    @Override
    public final CompleteRec packageAt(String qualifier) {
        return rootActorSystem.packageAt(qualifier);
    }

    @Override
    public Map<String, CompiledPackage> packages() {
        return packagesByQualifier;
    }

    @Override
    public final TorqCompilerParsed parse() throws Exception {
        if (state != State.READY) {
            throw new TorqCompilerError("Cannot parse at state: " + state, messages);
        }
        addInfoMessage("Parsing source modules");
        for (SourceFileBroker fileBroker : workspace) {
            for (List<FileName> root : fileBroker.roots()) {
                List<FileName> files = fileBroker.list(root);
                for (FileName file : files) {
                    parse(fileBroker, root, file);
                }
            }
        }
        addInfoMessage("Done parsing source modules");
        this.state = State.PARSED;
        return this;
    }

    private void parse(SourceFileBroker fileBroker, List<FileName> folderPath, FileName fileName) throws Exception {
        if (fileName.type() == FileType.FOLDER) {
            parseFolder(fileBroker, folderPath, fileName);
        } else if (fileName.type() == FileType.SOURCE && isTorqSourceFile(fileName.value())) {
            parseTorqSource(fileBroker, folderPath, fileName);
        } else {
            addWarnMessage("Skipping unknown file type: " + fileName.value());
        }
    }

    private void parseFolder(SourceFileBroker fileBroker, List<FileName> folderPath, FileName fileName) throws Exception {
        List<FileName> children = fileBroker.list(SourceFileBroker.append(folderPath, fileName));
        for (FileName child : children) {
            parse(fileBroker, SourceFileBroker.append(folderPath, fileName), child);
        }
    }

    private void parseTorqSource(SourceFileBroker fileBroker, List<FileName> folderPath, FileName fileName) throws Exception {
        List<FileName> absolutePath = SourceFileBroker.append(folderPath, fileName);
        SourceString source = fileBroker.source(absolutePath);
        String absolutePathFormatted = formatFileNamesAsPath(absolutePath);
        addTraceMessage("Parsing source module: " + absolutePathFormatted);
        Parser parser = new Parser(source);
        List<FileName> qualifiedTorqFile = fileBroker.trimRoot(absolutePath);
        if (qualifiedTorqFile.size() < 2) {
            throw new TorqCompilerError("Missing package", messages);
        }
        List<FileName> torqPackage = qualifiedTorqFile.subList(0, qualifiedTorqFile.size() - 1);
        ModuleStmt moduleStmt = parser.parseModule();
        if (!equalsPackageStmt(torqPackage, moduleStmt.packageStmt)) {
            throw new TorqCompilerError("Package folder does not match package statement", messages);
        }
        String torqFileName = ListTools.last(qualifiedTorqFile).value();
        String simpleTorqFileName = torqFileName.substring(0, torqFileName.lastIndexOf(DOT_TORQ));
        String qualifiedName;
        if (qualifiedTorqFile.size() == 1) {
            qualifiedName = simpleTorqFileName;
        } else {
            qualifiedName = formatFileNamesAsQualifier(qualifiedTorqFile.subList(0, qualifiedTorqFile.size() - 1)) +
                "." + simpleTorqFileName;
        }
        modulesByAbsolutePath.put(absolutePathFormatted,
            CompiledModule.createAfterParse(qualifiedName, absolutePath, qualifiedTorqFile, torqPackage, fileName, moduleStmt));
    }

    @Override
    public final TorqCompilerReady setLoggingLevel(MessageLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
        return this;
    }

    @Override
    public final TorqCompilerReady setWorkspace(List<SourceFileBroker> workspace) {
        if (state != State.READY) {
            throw new TorqCompilerError("Cannot setWorkspace at state: " + state, messages);
        }
        this.workspace = nullSafeCopyOf(workspace);
        return this;
    }

    public final List<SourceFileBroker> workspace() {
        return workspace;
    }

    private enum State {
        READY,
        PARSED,
        GENERATED,
        COLLECTED,
        BUNDLED,
    }

    private record FindExportResult(Lang lang, String stereotype, String nativeModule) {
    }

}
