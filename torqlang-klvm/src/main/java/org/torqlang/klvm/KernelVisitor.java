/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.klvm;

public interface KernelVisitor<T, R> {

    R visitActInstr(ActInstr kernel, T state);

    R visitActorCfg(ActorCfg kernel, T state);

    R visitActorCtor(ActorCtor kernel, T state);

    R visitAddInstr(AddInstr kernel, T state);

    R visitApplyInstr(ApplyInstr kernel, T state);

    R visitBindCompleteToCompleteInstr(BindCompleteToCompleteInstr kernel, T state);

    R visitBindCompleteToIdentInstr(BindCompleteToIdentInstr kernel, T state);

    R visitBindCompleteToValueOrVarInstr(BindCompleteToValueOrVarInstr kernel, T state);

    R visitBindIdentToIdentInstr(BindIdentToIdentInstr kernel, T state);

    R visitCaseElseInstr(CaseElseInstr kernel, T state);

    R visitCaseInstr(CaseInstr kernel, T state);

    R visitCatchInstr(CatchInstr kernel, T state);

    R visitClosure(Closure kernel, T state);

    R visitCreateActorCtorInstr(CreateActorCtorInstr kernel, T state);

    R visitCreateProcInstr(CreateProcInstr kernel, T state);

    R visitCreateRecInstr(CreateRecInstr kernel, T state);

    R visitCreateTupleInstr(CreateTupleInstr kernel, T state);

    R visitDebugInstr(DebugInstr kernel, T state);

    R visitDisentailsInstr(DisentailsInstr kernel, T state);

    R visitDivideInstr(DivideInstr kernel, T state);

    R visitEntailsInstr(EntailsInstr kernel, T state);

    R visitEnv(Env kernel, T state);

    R visitFailedValue(FailedValue kernel, T state);

    R visitFieldDef(FieldDef kernel, T state);

    R visitFieldPtn(FieldPtn kernel, T state);

    R visitGetCellValueInstr(GetCellValueInstr kernel, T state);

    R visitGreaterThanOrEqualToInstr(GreaterThanOrEqualToInstr kernel, T state);

    R visitGreaterThanInstr(GreaterThanInstr kernel, T state);

    R visitIdent(Ident kernel, T state);

    R visitIdentDef(IdentDef kernel, T state);

    R visitIdentPtn(IdentPtn kernel, T state);

    R visitIfElseInstr(IfElseInstr kernel, T state);

    R visitIfInstr(IfInstr kernel, T state);

    R visitJumpCatchInstr(JumpCatchInstr kernel, T state);

    R visitJumpThrowInstr(JumpThrowInstr kernel, T state);

    R visitLessThanOrEqualToInstr(LessThanOrEqualToInstr kernel, T state);

    R visitLessThanInstr(LessThanInstr kernel, T state);

    R visitLocalInstr(LocalInstr kernel, T state);

    R visitModuloInstr(ModuloInstr kernel, T state);

    R visitMultiplyInstr(MultiplyInstr kernel, T state);

    R visitNegateInstr(NegateInstr kernel, T state);

    R visitNotInstr(NotInstr kernel, T state);

    R visitObj(Obj kernel, T state);

    R visitOpaqueValue(OpaqueValue kernel, T state);

    R visitProc(Proc kernel, T state);

    R visitProcDef(ProcDef kernel, T state);

    R visitRec(Rec kernel, T state);

    R visitRecDef(RecDef kernel, T state);

    R visitRecPtn(RecPtn kernel, T state);

    R visitResolvedFieldPtn(ResolvedFieldPtn kernel, T state);

    R visitResolvedIdentPtn(ResolvedIdentPtn kernel, T state);

    R visitResolvedRecPtn(ResolvedRecPtn kernel, T state);

    R visitScalar(Scalar kernel, T state);

    R visitSelectAndApplyInstr(SelectAndApplyInstr kernel, T state);

    R visitSelectInstr(SelectInstr kernel, T state);

    R visitSeqInstr(SeqInstr kernel, T state);

    R visitSetCellValueInstr(SetCellValueInstr kernel, T state);

    R visitSkipInstr(SkipInstr kernel, T state);

    R visitStack(Stack kernel, T state);

    R visitSubtractInstr(SubtractInstr kernel, T state);

    R visitThrowInstr(ThrowInstr kernel, T state);

    R visitTryInstr(TryInstr kernel, T state);

    R visitTupleDef(TupleDef kernel, T state);

    R visitValueDef(ValueDef kernel, T state);

    R visitVar(Var kernel, T state);

    R visitVarSet(VarSet kernel, T state);
}
