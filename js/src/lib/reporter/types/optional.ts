export type OptionalIfEmpty<T, R = never> = keyof T extends R ? R : T;

export type IsNever<T> = [T] extends [never] ? true : false;

export type If<Type extends boolean, IfBranch, ElseBranch> =
  IsNever<Type> extends true ? ElseBranch
  : Type extends true ? IfBranch
  : ElseBranch;
