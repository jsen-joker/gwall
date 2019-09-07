import request from '@/utils/request';
import { Params } from './data.d';
import { stringify } from 'qs';

export async function getRoutes(params: Params) {
  return request(`/api/1/authentication/auth/gateway/routes?${stringify(params)}`);
}

export async function getRoute(params: Params) {
  return request(`/api/1/authentication/auth/gateway/routes/${params.id}`);
}

export async function getInstances() {
  return request('/api/1/authentication/auth/naming/instances');
}

export async function removeRule(params: Params) {
  return request(`/api/1/authentication/auth/gateway/routes/${params.id}`, {
    method: 'DELETE',
  });
}

export async function updateRule(params: Params) {
  return request('/api/1/authentication/auth/gateway/routes', {
    method: 'POST',
    data: {
      ...params,
    },
  });
}
