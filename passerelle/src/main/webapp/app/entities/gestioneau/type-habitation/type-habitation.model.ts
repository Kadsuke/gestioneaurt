export interface ITypeHabitation {
  id?: number;
  libelle?: string;
}

export class TypeHabitation implements ITypeHabitation {
  constructor(public id?: number, public libelle?: string) {}
}

export function getTypeHabitationIdentifier(typeHabitation: ITypeHabitation): number | undefined {
  return typeHabitation.id;
}
