export interface IDirectionRegionale {
  id?: number;
  libelle?: string;
  responsable?: string;
  contact?: string;
}

export class DirectionRegionale implements IDirectionRegionale {
  constructor(public id?: number, public libelle?: string, public responsable?: string, public contact?: string) {}
}

export function getDirectionRegionaleIdentifier(directionRegionale: IDirectionRegionale): number | undefined {
  return directionRegionale.id;
}
