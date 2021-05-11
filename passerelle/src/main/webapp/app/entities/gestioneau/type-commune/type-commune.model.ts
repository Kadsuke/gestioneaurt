export interface ITypeCommune {
  id?: number;
  libelle?: string;
}

export class TypeCommune implements ITypeCommune {
  constructor(public id?: number, public libelle?: string) {}
}

export function getTypeCommuneIdentifier(typeCommune: ITypeCommune): number | undefined {
  return typeCommune.id;
}
