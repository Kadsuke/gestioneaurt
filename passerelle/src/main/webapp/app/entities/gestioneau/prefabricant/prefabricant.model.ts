export interface IPrefabricant {
  id?: number;
  libelle?: string;
}

export class Prefabricant implements IPrefabricant {
  constructor(public id?: number, public libelle?: string) {}
}

export function getPrefabricantIdentifier(prefabricant: IPrefabricant): number | undefined {
  return prefabricant.id;
}
