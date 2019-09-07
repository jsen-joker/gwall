import { AnyAction, Reducer } from 'redux';
import { EffectsCommandMap } from 'dva';
import { getRoutes, getInstances, removeRule, updateRule, getRoute } from './service';

import { RouteDefinitionData } from './data.d';

export interface StateType {
  data: RouteDefinitionData;
}

export type Effect = (
  action: AnyAction,
  effects: EffectsCommandMap & { select: <T>(func: (state: StateType) => T) => T },
) => void;

export interface ModelType {
  namespace: string;
  state: StateType;
  effects: {
    fetch: Effect;
    fetchServices: Effect;
    remove: Effect;
    update: Effect;
    justGetRoute: Effect;
  };
  reducers: {
    save: Reducer<StateType>;
  };
}

const Model: ModelType = {
  namespace: 'listTableList',

  state: {
    data: {
      data: [],
      services: [],
    },
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      const response = yield call(getRoutes, payload);
      const instances = yield call(getInstances);
      response.services = instances.data || [];
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *fetchServices({ payload, callback }, { call, put }) {
      const instances = yield call(getInstances);
      const response = {
        code: 0,
        services: instances.data || [],
      };
      if (callback) {
        callback(instances.data || []);
      }
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *remove({ payload, callback }, { call, put }) {
      const response = yield call(removeRule, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      if (callback) callback();
    },
    *update({ payload, callback }, { call, put }) {
      const response = yield call(updateRule, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      if (callback) callback(response);
    },
    *justGetRoute({ payload, callback }, { call }) {
      if (callback) {
        const response = yield call(getRoute, payload);
        callback(response);
      }
    },
  },

  reducers: {
    save(state, { payload: { data = false, services = false } }) {
      const d = data ? data : state.data.data;
      const s = services ? services : state.data.services;
      return {
        ...state,
        data: { data: d, services: s },
      };
    },
  },
};

export default Model;
