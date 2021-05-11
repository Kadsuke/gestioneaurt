export interface IRegion {
  id?: number;
  libelle?: string;
}

export class Region implements IRegion {
  constructor(public id?: number, public libelle?: string) {}
}

export function getRegionIdentifier(region: IRegion): number | undefined {
  return region.id;
}
