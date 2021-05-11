import { IProvince } from 'app/entities/gestioneau/province/province.model';
import { ITypeCommune } from 'app/entities/gestioneau/type-commune/type-commune.model';

export interface ICommune {
  id?: number;
  libelle?: string;
  province?: IProvince | null;
  typecommune?: ITypeCommune | null;
}

export class Commune implements ICommune {
  constructor(public id?: number, public libelle?: string, public province?: IProvince | null, public typecommune?: ITypeCommune | null) {}
}

export function getCommuneIdentifier(commune: ICommune): number | undefined {
  return commune.id;
}
