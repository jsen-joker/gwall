import request from '@/utils/request';
import { Params } from './data.d';

export async function getRoutes() {
  return request('/api/gateway/guard/100/gwall-gateway/routes');
}

export async function getInstances() {
  return request('/api/gateway/guard/100/gwall-naming/instances');
}

export async function removeRule(params: Params) {
  return request(`/api/gateway/guard/100/gwall-gateway/routes/${params.id}`, {
    method: 'DELETE',
  });
}

export async function addRule(params: Params) {
  return updateRule(params);
}

export async function updateRule(params: Params) {
  return request('/api/gateway/guard/100/gwall-gateway/routes', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}
