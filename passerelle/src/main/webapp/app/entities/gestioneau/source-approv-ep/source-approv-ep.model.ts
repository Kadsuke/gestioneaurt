export interface ISourceApprovEp {
  id?: number;
  libelle?: string;
}

export class SourceApprovEp implements ISourceApprovEp {
  constructor(public id?: number, public libelle?: string) {}
}

export function getSourceApprovEpIdentifier(sourceApprovEp: ISourceApprovEp): number | undefined {
  return sourceApprovEp.id;
}
