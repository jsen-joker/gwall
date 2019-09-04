// export interface PredicateDefinition {
//   name: string,
//   args: {[index:string]: string},
// }
// export interface FilterDefinition {
//   name: string,
//   args: {[index:string]: string},
// }
export interface RouteDefinition {
  id: string;
  predicates: string[];
  filters: string[];
  uri: string;
  order: number;
  createdAt: Date;
  updatedAt: Date;
  desc: string;
  status: number;
  system: boolean;
}

export interface RouteDefinitionData {
  data: RouteDefinition[];
  services: string[];
}

export interface Params {
  id: string;
}
