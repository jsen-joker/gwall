import {
  Form,
  Icon,
  Input,
  Popover,
  Select,
  message,
  Card,
  Row,
  Col,
  InputNumber,
  AutoComplete,
  Button,
} from 'antd';
import React, { Component } from 'react';

import { Dispatch } from 'redux';
import { FormComponentProps } from 'antd/es/form';
import { connect } from 'dva';
import styles from './style.less';
import { StateType } from '../model';
import router from 'umi/router';

import { ResponseBase } from '@/utils/response.d.ts';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import TextArea from 'antd/es/input/TextArea';
import FooterToolbar from './components/FooterToolbar';
import TableForm from './components/TableForm';
import { withRouter } from 'react-router';

const { Option } = Select;
const fieldLabels = {
  id: '路由名字',
  status: '路由状态',
  order: '优先级',
  desc: '备注',
  uri: '选择服务',
};

const typesFilter = ['Authorization'];
const typesPredicates = ['Path'];

interface AdvancedFormProps extends FormComponentProps {
  dispatch: Dispatch<any>;
  submitting: boolean;
  listTableList: StateType;
}

@connect(
  ({
    listTableList,
    loading,
  }: {
    listTableList: StateType;
    loading: { effects: { [key: string]: boolean } };
  }) => ({
    listTableList,
    submitting: loading.effects['listTableList/update'],
  }),
)
class AdvancedForm extends Component<AdvancedFormProps> {
  state = {
    width: '100%',
    serviceDatasource: [],
    initForm: {
      id: '',
      status: 1,
      order: 1,
      uri: '',
      desc: '',
      predicates: [{ type: '', value: '', editable: true, isNew: true }],
      filters: [{ type: '', value: '', editable: true, isNew: true }],
    },
  };

  componentDidMount() {
    const { dispatch } = this.props;
    const {
      listTableList: {
        data: { services = [] },
      },
    } = this.props;
    if (services.length === 0) {
      dispatch({
        type: 'listTableList/fetchServices',
        callback: (data: string[]) => {
          this.setState({ serviceDatasource: data.map(item => `lb://${item}`) });
        },
      });
    } else {
      this.setState({
        serviceDatasource: services.map(item => `lb://${item}`),
      });
    }
    const {
      history: {
        location: {
          query: { id = false },
        },
      },
    } = this.props;
    if (id) {
      dispatch({
        type: 'listTableList/justGetRoute',
        payload: {
          id: id,
        },
        callback: (data: ResponseBase) => {
          console.log(data);
          if (data.code === 0) {
            const d = data.data;
            d.predicates = d.predicates.map((item: string) => {
              const sp = item.indexOf('=');
              if (sp >= 0) {
                return {
                  type: item.substring(0, sp),
                  value: item.substring(sp + 1),
                  editable: false,
                  isNew: false,
                };
              } else {
                return { type: item, value: '', editable: false, isNew: false };
              }
            });
            d.filters = d.filters.map((item: string) => {
              const sp = item.indexOf('=');
              if (sp >= 0) {
                return {
                  type: item.substring(0, sp),
                  value: item.substring(sp + 1),
                  editable: false,
                  isNew: false,
                };
              } else {
                return { type: item, value: '', editable: false, isNew: false };
              }
            });
            this.setState({ initForm: d });
          }
        },
      });
    }
    window.addEventListener('resize', this.resizeFooterToolbar, { passive: true });
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.resizeFooterToolbar);
  }

  onSearchService = (searchText: string) => {
    const {
      listTableList: {
        data: { services },
      },
    } = this.props;
    if (searchText.startsWith('lb://')) {
      searchText = searchText.substring(5);
    }
    this.setState({
      serviceDatasource: services
        .filter(item => item.indexOf(searchText) >= 0)
        .map(item => `lb://${item}`),
    });
  };

  getErrorInfo = () => {
    const {
      form: { getFieldsError },
    } = this.props;
    const errors = getFieldsError();
    const errorCount = Object.keys(errors).filter(key => errors[key]).length;
    if (!errors || errorCount === 0) {
      return null;
    }
    const scrollToField = (fieldKey: string) => {
      const labelNode = document.querySelector(`label[for="${fieldKey}"]`);
      if (labelNode) {
        labelNode.scrollIntoView(true);
      }
    };
    const errorList = Object.keys(errors).map(key => {
      if (!errors[key]) {
        return null;
      }
      const errorMessage = errors[key] || [];
      return (
        <li key={key} className={styles.errorListItem} onClick={() => scrollToField(key)}>
          <Icon type="cross-circle-o" className={styles.errorIcon} />
          <div className={styles.errorMessage}>{errorMessage[0]}</div>
          <div className={styles.errorField}>{fieldLabels[key]}</div>
        </li>
      );
    });
    return (
      <span className={styles.errorIcon}>
        <Popover
          title="表单校验信息"
          content={errorList}
          overlayClassName={styles.errorPopover}
          trigger="click"
          getPopupContainer={(trigger: HTMLElement) => {
            if (trigger && trigger.parentNode) {
              return trigger.parentNode as HTMLElement;
            }
            return trigger;
          }}
        >
          <Icon type="exclamation-circle" />
        </Popover>
        {errorCount}
      </span>
    );
  };

  resizeFooterToolbar = () => {
    requestAnimationFrame(() => {
      const sider = document.querySelectorAll('.ant-layout-sider')[0] as HTMLDivElement;
      if (sider) {
        const width = `calc(100% - ${sider.style.width})`;
        const { width: stateWidth } = this.state;
        if (stateWidth !== width) {
          this.setState({ width });
        }
      }
    });
  };

  validate = () => {
    const {
      form: { validateFieldsAndScroll },
      dispatch,
    } = this.props;
    validateFieldsAndScroll((error, values) => {
      if (!error) {
        const body = { ...values };
        if (
          !values.predicates ||
          values.predicates.filter(item => !item.isNew && item.type && item.type !== '').length ===
            0
        ) {
          message.info('Predicate is null');
          return;
        }
        body.predicates = values.predicates
          .filter(item => !item.isNew && item.type && item.type !== '')
          .map(item => {
            if (item.value && item.value !== '') {
              return `${item.type}=${item.value}`;
            } else {
              return item.type;
            }
          });
        body.filters = values.filters
          .filter(item => !item.isNew && item.type && item.type !== '')
          .map(item => {
            if (item.value && item.value !== '') {
              return `${item.type}=${item.value}`;
            } else {
              return item.type;
            }
          });

        dispatch({
          type: 'listTableList/update',
          payload: body,
          callback: (response: ResponseBase) => {
            if (response.code === 0) {
              message.info('成功');
              router.goBack();
            } else {
              message.warn(response.msg || '失败');
            }
          },
        });
      }
    });
  };

  render() {
    const {
      form: { getFieldDecorator },
      submitting,
    } = this.props;
    const {
      width,
      initForm: { id, status, order, uri, desc, predicates, filters },
    } = this.state;
    return (
      <>
        <PageHeaderWrapper content="创建或者更新路由，使用spring cloud gateway的标准路由格式">
          <Card title="Basic" className={styles.card} bordered={false}>
            <Form layout="vertical" hideRequiredMark>
              <Row gutter={16}>
                <Col lg={6} md={12} sm={24}>
                  <Form.Item label={fieldLabels.id}>
                    {getFieldDecorator('id', {
                      rules: [{ required: true, message: '请输入唯一路由名字' }],
                      initialValue: id,
                    })(<Input placeholder="请输入唯一路由名字" />)}
                  </Form.Item>
                </Col>
                <Col xl={{ span: 6, offset: 2 }} lg={{ span: 8 }} md={{ span: 12 }} sm={24}>
                  <Form.Item label={fieldLabels.status}>
                    {getFieldDecorator('status', {
                      rules: [{ required: true, message: '请选择路由状态' }],
                      initialValue: status,
                    })(
                      <Select placeholder="路由状态">
                        <Option value={0}>未运行</Option>
                        <Option value={1}>运行中</Option>
                      </Select>,
                    )}
                  </Form.Item>
                </Col>
                <Col xl={{ span: 8, offset: 2 }} lg={{ span: 10 }} md={{ span: 24 }} sm={24}>
                  <Form.Item label={fieldLabels.order}>
                    {getFieldDecorator('order', {
                      rules: [{ required: true, message: '请输入优先级' }],
                      initialValue: order,
                    })(<InputNumber style={{ width: '100%' }} placeholder="请输入优先级" />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col lg={12} md={12} sm={24}>
                  <Form.Item label={fieldLabels.uri}>
                    {getFieldDecorator('uri', {
                      rules: [{ required: true, message: '请输入唯一路由名字' }],
                      initialValue: uri,
                    })(
                      <AutoComplete
                        dataSource={this.state.serviceDatasource}
                        style={{ width: '100%' }}
                        onSearch={this.onSearchService}
                        placeholder="服务（http://xxxx.com）"
                      />,
                    )}
                  </Form.Item>
                </Col>
                <Col lg={12} md={12} sm={24}>
                  <Form.Item label={fieldLabels.desc}>
                    {getFieldDecorator('desc', {
                      rules: [
                        { required: true, message: '请输入至少五个字符的路由描述！', min: 5 },
                      ],
                      initialValue: desc,
                    })(<TextArea rows={4} placeholder="请输入至少五个字符" />)}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card title="Predicates" className={styles.card} bordered={false}>
            {getFieldDecorator('predicates', {
              initialValue: predicates,
            })(<TableForm types={typesPredicates} />)}
          </Card>
          <Card title="Filters" bordered={false}>
            {getFieldDecorator('filters', {
              initialValue: filters,
            })(<TableForm types={typesFilter} />)}
          </Card>
        </PageHeaderWrapper>
        <FooterToolbar style={{ width }}>
          {this.getErrorInfo()}
          <Button type="primary" onClick={this.validate} loading={submitting}>
            提交
          </Button>
        </FooterToolbar>
      </>
    );
  }
}

export default Form.create<AdvancedFormProps>()(withRouter(AdvancedForm));
