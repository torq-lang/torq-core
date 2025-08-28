/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.lang;

/*
 * Many specific types are visited using generic methods. All the methods in the first table below are replaced by the
 * methods in the second table:
 *
 *     SPECIFIC METHODS
 *     Type                AsType              AsExpr              AsPat
 *     ------------------  ------------------  ------------------  ------------------
 *     visitAnyType
 *     visitArrayType
 *     visitBoolType      visitBoolAsType      visitBoolAsExpr     visitBoolAsPat
 *     visitCharType      visitCharAsType      visitCharAsExpr
 *     visitDec128Type    visitDec128AsType    visitDec128AsExpr
 *     visitEofType       visitEofAsType       visitEofAsExpr      visitEofAsPat
 *     visitFlt32Type     visitFlt32AsType     [1]
 *     visitFlt64Type     visitFlt64AsType     visitFlt64AsExpr
 *     visitInt32Type     visitInt32AsType     [1]
 *     visitInt64Type     visitInt64AsType     visitInt64AsExpr    visitInt64AsPat
 *     visitNullType      visitNullAsType      visitNullAsExpr     visitNullAsPat
 *     visitStrType       visitStrAsType       visitStrAsExpr      visitStrAsPat
 *     visitTokenType [2]
 *
 *     GENERAL METHODS
 *     Type                AsType              AsExpr              AsPat
 *     ------------------  ------------------  ------------------  ------------------
 *     visitIdentAsType    visitScalarAsType   visitScalarAsExpr   visitFeatureAsPat [3]
 *
 *     [1] 32-bit values are carried by the tool chain as subtypes of 64-bit values until the concrete value
 *         is needed.
 *
 *     [2] Tokens are unforgeable so there are no "as" methods to express its value.
 *
 *     [3] Currently, Int64 is a subtype of Feature in the KLVM, and Int32 is a subtype of Int64 according to the
 *         Liskov Substitution Principle (LSP). In spite of this, our validation process only allows 32-bit integers as
 *         features because most containers, such as arrays, only allow a 32-bit range of values.
 */
public interface LangVisitor<T, R> {

    R visitActExpr(ActExpr lang, T state);

    R visitActorExpr(ActorExpr lang, T state);

    R visitActorStmt(ActorStmt lang, T state);

    R visitAndExpr(AndExpr lang, T state);

    R visitApplyLang(ApplyLang lang, T state);

    R visitAskStmt(AskStmt lang, T state);

    R visitBeginLang(BeginLang lang, T state);

    R visitBreakStmt(BreakStmt lang, T state);

    R visitCaseClause(CaseClause lang, T state);

    R visitCaseLang(CaseLang lang, T state);

    R visitCatchClause(CatchClause lang, T state);

    R visitContinueStmt(ContinueStmt lang, T state);

    R visitDotSelectExpr(DotSelectExpr lang, T state);

    R visitFeatureAsPat(FeatureAsPat lang, T state);

    R visitFieldExpr(FieldExpr lang, T state);

    R visitFieldPat(FieldPat lang, T state);

    R visitFieldType(FieldType lang, T state);

    R visitForStmt(ForStmt lang, T state);

    R visitFuncExpr(FuncExpr lang, T state);

    R visitFuncStmt(FuncStmt lang, T state);

    R visitFuncType(FuncType lang, T state);

    R visitGroupExpr(GroupExpr lang, T state);

    R visitIdentAsExpr(IdentAsExpr lang, T state);

    R visitIdentAsPat(IdentAsPat lang, T state);

    R visitIdentAsProtocolType(IdentAsProtocolType lang, T state);

    R visitIdentAsType(IdentAsType lang, T state);

    R visitIdentVarDecl(IdentVarDecl lang, T state);

    R visitIfClause(IfClause lang, T state);

    R visitIfLang(IfLang lang, T state);

    R visitImportName(ImportName lang, T state);

    R visitImportStmt(ImportStmt lang, T state);

    R visitIndexSelectExpr(IndexSelectExpr lang, T state);

    R visitInitVarDecl(InitVarDecl lang, T state);

    R visitIntersectionProtocolType(IntersectionProtocolType lang, T state);

    R visitIntersectionType(IntersectionType lang, T state);

    R visitLocalLang(LocalLang lang, T state);

    R visitMetaField(MetaField lang, T state);

    R visitMetaRec(MetaRec lang, T state);

    R visitMetaTuple(MetaTuple lang, T state);

    R visitModuleStmt(ModuleStmt lang, T state);

    R visitNewExpr(NewExpr lang, T state);

    R visitObjType(ObjType lang, T state);

    R visitOrExpr(OrExpr lang, T state);

    R visitPackageStmt(PackageStmt lang, T state);

    R visitProcExpr(ProcExpr lang, T state);

    R visitProcStmt(ProcStmt lang, T state);

    R visitProcType(ProcType lang, T state);

    R visitProductExpr(ProductExpr lang, T state);

    R visitProtocolStmt(ProtocolStmt lang, T state);

    R visitProtocolTypeAskHandler(ProtocolTypeAskHandler lang, T state);

    R visitProtocolTypeCtor(ProtocolTypeCtor lang, T state);

    R visitProtocolTypeStreamHandler(ProtocolTypeStreamHandler lang, T state);

    R visitProtocolTypeStruct(ProtocolTypeStruct lang, T state);

    R visitProtocolTypeTellHandler(ProtocolTypeTellHandler lang, T state);

    R visitRecExpr(RecExpr lang, T state);

    R visitRecPat(RecPat lang, T state);

    R visitRecType(RecType lang, T state);

    R visitRecTypeExpr(RecTypeExpr lang, T state);

    R visitRelationalExpr(RelationalExpr lang, T state);

    R visitRespondStmt(RespondStmt lang, T state);

    R visitReturnStmt(ReturnStmt lang, T state);

    R visitScalarAsExpr(ScalarAsExpr lang, T state);

    R visitScalarAsType(ScalarAsType lang, T state);

    R visitSelectAndApplyLang(SelectAndApplyLang lang, T state);

    R visitSeqLang(SeqLang lang, T state);

    R visitSetCellValueStmt(SetCellValueStmt lang, T state);

    R visitSkipStmt(SkipStmt lang, T state);

    R visitSpawnExpr(SpawnExpr lang, T state);

    R visitSumExpr(SumExpr lang, T state);

    R visitTellStmt(TellStmt lang, T state);

    R visitThrowLang(ThrowLang lang, T state);

    R visitTryLang(TryLang lang, T state);

    R visitTupleExpr(TupleExpr lang, T state);

    R visitTuplePat(TuplePat lang, T state);

    R visitTupleType(TupleType lang, T state);

    R visitTupleTypeExpr(TupleTypeExpr lang, T state);

    R visitTypeCtor(TypeCtor lang, T state);

    R visitTypeDecl(TypeDecl lang, T state);

    R visitTypeParam(TypeParam lang, T state);

    R visitTypeStmt(TypeStmt lang, T state);

    R visitUnaryExpr(UnaryExpr lang, T state);

    R visitUnifyStmt(UnifyStmt lang, T state);

    R visitUnionType(UnionType lang, T state);

    R visitVarStmt(VarStmt lang, T state);

    R visitWhileStmt(WhileStmt lang, T state);
}
