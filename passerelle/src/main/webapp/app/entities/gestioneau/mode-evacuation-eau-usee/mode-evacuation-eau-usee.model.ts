export interface IModeEvacuationEauUsee {
  id?: number;
  libelle?: string;
}

export class ModeEvacuationEauUsee implements IModeEvacuationEauUsee {
  constructor(public id?: number, public libelle?: string) {}
}

export function getModeEvacuationEauUseeIdentifier(modeEvacuationEauUsee: IModeEvacuationEauUsee): number | undefined {
  return modeEvacuationEauUsee.id;
}
