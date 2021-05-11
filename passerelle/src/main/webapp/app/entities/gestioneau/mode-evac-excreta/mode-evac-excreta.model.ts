export interface IModeEvacExcreta {
  id?: number;
  libelle?: string;
}

export class ModeEvacExcreta implements IModeEvacExcreta {
  constructor(public id?: number, public libelle?: string) {}
}

export function getModeEvacExcretaIdentifier(modeEvacExcreta: IModeEvacExcreta): number | undefined {
  return modeEvacExcreta.id;
}
