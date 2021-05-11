import * as dayjs from 'dayjs';
import { IParcelle } from 'app/entities/gestioneau/parcelle/parcelle.model';
import { IPrevision } from 'app/entities/gestioneau/prevision/prevision.model';
import { INatureOuvrage } from 'app/entities/gestioneau/nature-ouvrage/nature-ouvrage.model';
import { ITypeHabitation } from 'app/entities/gestioneau/type-habitation/type-habitation.model';
import { ISourceApprovEp } from 'app/entities/gestioneau/source-approv-ep/source-approv-ep.model';
import { IModeEvacuationEauUsee } from 'app/entities/gestioneau/mode-evacuation-eau-usee/mode-evacuation-eau-usee.model';
import { IModeEvacExcreta } from 'app/entities/gestioneau/mode-evac-excreta/mode-evac-excreta.model';
import { IMacon } from 'app/entities/gestioneau/macon/macon.model';
import { IPrefabricant } from 'app/entities/gestioneau/prefabricant/prefabricant.model';

export interface IFicheSuiviOuvrage {
  id?: number;
  prjAppuis?: string;
  nomBenef?: string;
  prenomBenef?: string;
  professionBenef?: string;
  nbUsagers?: number;
  contacts?: string;
  longitude?: number;
  latitude?: number;
  dateRemiseDevis?: dayjs.Dayjs;
  dateDebutTravaux?: dayjs.Dayjs;
  dateFinTravaux?: dayjs.Dayjs;
  rue?: string | null;
  porte?: string | null;
  coutMenage?: string;
  subvOnea?: number;
  subvProjet?: number;
  autreSubv?: number;
  toles?: number;
  animateur?: string;
  superviseur?: string;
  controleur?: string;
  parcelle?: IParcelle | null;
  prevision?: IPrevision | null;
  natureouvrage?: INatureOuvrage | null;
  typehabitation?: ITypeHabitation | null;
  sourceapprovep?: ISourceApprovEp | null;
  modeevacuationeauusee?: IModeEvacuationEauUsee | null;
  modeevacexcreta?: IModeEvacExcreta | null;
  macon?: IMacon | null;
  prefabricant?: IPrefabricant | null;
}

export class FicheSuiviOuvrage implements IFicheSuiviOuvrage {
  constructor(
    public id?: number,
    public prjAppuis?: string,
    public nomBenef?: string,
    public prenomBenef?: string,
    public professionBenef?: string,
    public nbUsagers?: number,
    public contacts?: string,
    public longitude?: number,
    public latitude?: number,
    public dateRemiseDevis?: dayjs.Dayjs,
    public dateDebutTravaux?: dayjs.Dayjs,
    public dateFinTravaux?: dayjs.Dayjs,
    public rue?: string | null,
    public porte?: string | null,
    public coutMenage?: string,
    public subvOnea?: number,
    public subvProjet?: number,
    public autreSubv?: number,
    public toles?: number,
    public animateur?: string,
    public superviseur?: string,
    public controleur?: string,
    public parcelle?: IParcelle | null,
    public prevision?: IPrevision | null,
    public natureouvrage?: INatureOuvrage | null,
    public typehabitation?: ITypeHabitation | null,
    public sourceapprovep?: ISourceApprovEp | null,
    public modeevacuationeauusee?: IModeEvacuationEauUsee | null,
    public modeevacexcreta?: IModeEvacExcreta | null,
    public macon?: IMacon | null,
    public prefabricant?: IPrefabricant | null
  ) {}
}

export function getFicheSuiviOuvrageIdentifier(ficheSuiviOuvrage: IFicheSuiviOuvrage): number | undefined {
  return ficheSuiviOuvrage.id;
}
