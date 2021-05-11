import { IRegion } from 'app/entities/gestioneau/region/region.model';

export interface IProvince {
  id?: number;
  libelle?: string;
  region?: IRegion | null;
}

export class Province implements IProvince {
  constructor(public id?: number, public libelle?: string, public region?: IRegion | null) {}
}

export function getProvinceIdentifier(province: IProvince): number | undefined {
  return province.id;
}
