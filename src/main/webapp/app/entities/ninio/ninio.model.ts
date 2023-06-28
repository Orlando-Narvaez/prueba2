import dayjs from 'dayjs/esm';
import { IAdulto } from 'app/entities/adulto/adulto.model';

export interface INinio {
  id?: number;
  nombre?: string;
  apellido?: string;
  edad?: number | null;
  fechaNacimiento?: dayjs.Dayjs | null;
  adulto?: IAdulto | null;
}

export class Ninio implements INinio {
  constructor(
    public id?: number,
    public nombre?: string,
    public apellido?: string,
    public edad?: number | null,
    public fechaNacimiento?: dayjs.Dayjs | null,
    public adulto?: IAdulto | null
  ) {}
}

export function getNinioIdentifier(ninio: INinio): number | undefined {
  return ninio.id;
}
