import { ISecteur } from 'app/entities/gestioneau/secteur/secteur.model';

export interface ISection {
  id?: number;
  libelle?: string;
  secteur?: ISecteur | null;
}

export class Section implements ISection {
  constructor(public id?: number, public libelle?: string, public secteur?: ISecteur | null) {}
}

export function getSectionIdentifier(section: ISection): number | undefined {
  return section.id;
}
