export interface IMacon {
  id?: number;
  libelle?: string;
}

export class Macon implements IMacon {
  constructor(public id?: number, public libelle?: string) {}
}

export function getMaconIdentifier(macon: IMacon): number | undefined {
  return macon.id;
}
