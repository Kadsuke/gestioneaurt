export interface INatureOuvrage {
  id?: number;
  libelle?: string;
}

export class NatureOuvrage implements INatureOuvrage {
  constructor(public id?: number, public libelle?: string) {}
}

export function getNatureOuvrageIdentifier(natureOuvrage: INatureOuvrage): number | undefined {
  return natureOuvrage.id;
}
