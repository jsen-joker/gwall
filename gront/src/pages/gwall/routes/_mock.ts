import { Request, Response } from 'express';
import { RouteDefinition } from './data.d';

// mock tableListDataSource
let tableListDataSource: RouteDefinition[] = [];
let services: string[] = [];

for (let i = 0; i < 8; i += 1) {
  tableListDataSource.push({
    id: i + '',
    predicates: ['Path=/test/**'],
    filters: ['StripPrefix=1', 'Authentication'],
    uri: 'lb://service1',
    order: 0,
    createdAt: new Date(`2017-07-${Math.floor(i / 2) + 1}`),
    updatedAt: new Date(`2017-07-${Math.floor(i / 2) + 1}`),
    desc: '这是一段描述',
    status: Math.floor(Math.random() * 10) % 4,
  });
  services = ['service1', 'service2', 'service3'];
}

function getRule(req: Request, res: Response, u: string) {
  const result = {
    code: 0,
    data: tableListDataSource,
  };

  return res.json(result);
}
function getInstances(req: Request, res: Response, u: string) {
  const result = {
    code: 0,
    data: services,
  };

  return res.json(result);
}
function postRule(req: Request, res: Response, u: string, b: Request) {
  let url = u;
  if (!url || Object.prototype.toString.call(url) !== '[object String]') {
    // eslint-disable-next-line prefer-destructuring
    url = req.url;
  }

  const body = (b && b.body) || req.body;
  const { desc, id, predicates = [], filters = [], uri = '', order = 0, status = 1 } = body;

  /* eslint no-case-declarations:0 */
  let updated: boolean = false;
  tableListDataSource = tableListDataSource.map(item => {
    if (item.id === id) {
      updated = true;
      console.log(body);
      return { ...item, ...body };
    }
    return item;
  });
  if (!updated) {
    const i = Math.ceil(Math.random() * 10000);
    tableListDataSource.unshift({
      id,
      predicates,
      filters,
      uri,
      order,
      createdAt: new Date(`2017-07-${Math.floor(i / 2) + 1}`),
      updatedAt: new Date(`2017-07-${Math.floor(i / 2) + 1}`),
      desc,
      status,
    });
  }

  const result = {
    code: 0,
    data: tableListDataSource,
  };

  return res.json(result);
}

function deleteRule(req: Request, res: Response, u: string, b: Request) {
  let url = u;
  if (!url || Object.prototype.toString.call(url) !== '[object String]') {
    // eslint-disable-next-line prefer-destructuring
    url = req.url;
  }

  const { id } = req.params;
  tableListDataSource = tableListDataSource.filter(item => id.indexOf(item.id) === -1);

  const result = {
    code: 0,
    data: tableListDataSource,
  };

  return res.json(result);
}

export default {
  'GET /api/gateway/guard/100/gwall-gateway/routes': getRule,
  'GET /api/gateway/guard/100/gwall-naming/instances': getInstances,
  'POST /api/gateway/guard/100/gwall-gateway/routes': postRule,
  'DELETE /api/gateway/guard/100/gwall-gateway/routes': deleteRule,
};
