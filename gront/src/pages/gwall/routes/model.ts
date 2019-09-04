import { AnyAction, Reducer } from 'redux';
import { EffectsCommandMap } from 'dva';
import { addRule, getRoutes, getInstances, removeRule, updateRule } from './service';

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
    add: Effect;
    remove: Effect;
    update: Effect;
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
    *add({ payload, callback }, { call, put }) {
      const response = yield call(addRule, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      if (callback) callback();
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
      if (callback) callback();
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
