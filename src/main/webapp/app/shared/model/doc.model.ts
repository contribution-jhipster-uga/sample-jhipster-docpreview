import { Moment } from 'moment';

export interface IDoc {
  id?: number;
  title?: string;
  language?: string;
  description?: string;
  contentContentType?: string;
  content?: any;
  contentSha1?: string;
  numberOfPages?: number;
  createdAt?: Moment;
  updatedAt?: Moment;
}

export class Doc implements IDoc {
  constructor(
    public id?: number,
    public title?: string,
    public language?: string,
    public description?: string,
    public contentContentType?: string,
    public content?: any,
    public contentSha1?: string,
    public numberOfPages?: number,
    public createdAt?: Moment,
    public updatedAt?: Moment
  ) {}
}
