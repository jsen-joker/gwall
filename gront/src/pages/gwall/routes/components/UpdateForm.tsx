import {
  Button,
  Form,
  Input,
  Modal,
  Select,
  Steps,
  Checkbox,
  InputNumber,
  AutoComplete,
} from 'antd';
import React, { Component } from 'react';

import { FormComponentProps } from 'antd/es/form';
import { RouteDefinition } from '../data.d';

export interface FormValsType extends Partial<RouteDefinition> {
  path?: string;

  stripPrefix?: number;
  authentication?: boolean;
  authorization?: boolean;
  loginFacade?: boolean;
}

export interface UpdateFormProps extends FormComponentProps {
  handleUpdateModalVisible: (flag?: boolean, formVals?: FormValsType) => void;
  handleUpdate: (values: FormValsType) => void;
  updateModalVisible: boolean;
  values: Partial<RouteDefinition>;
  services: string[];
}
const FormItem = Form.Item;
const { Step } = Steps;
const { TextArea } = Input;
const { Option } = Select;

export interface UpdateFormState {
  formVals: FormValsType;
  currentStep: number;
  serviceDatasource: string[];
}

class UpdateForm extends Component<UpdateFormProps, UpdateFormState> {
  static defaultProps = {
    handleUpdate: () => {},
    handleUpdateModalVisible: () => {},
    values: {},
  };

  formLayout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 13 },
  };

  constructor(props: UpdateFormProps) {
    super(props);

    const {
      services,
      values: { id = '', desc = '', uri = '', predicates = [], filters = [], status = 1 },
    } = props;
    let path = '',
      stripPrefix: number = 0;
    let authentication = false,
      authorization = false,
      loginFacade = false;
    predicates.forEach(item => {
      if (item.startsWith('Path=')) {
        path = item.substring(5);
      }
    });
    filters.forEach(item => {
      if (item.startsWith('StripPrefix=')) {
        stripPrefix = parseInt(item.substring(12), 10);
      } else if (item.startsWith('Authentication')) {
        authentication = true;
      } else if (item.startsWith('Authorization')) {
        authorization = true;
      } else if (item.startsWith('LoginFacade')) {
        loginFacade = true;
      }
    });
    this.state = {
      formVals: {
        id: id,
        desc: desc,
        uri,
        path,
        stripPrefix,
        authentication,
        authorization,
        loginFacade,
        status,
      },
      currentStep: 0,
      serviceDatasource: services.map(item => `lb://${item}`),
    };
  }

  handleNext = (currentStep: number) => {
    const { form, handleUpdate } = this.props;
    const { formVals: oldValue } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const formVals = { ...oldValue, ...fieldsValue };
      this.setState(
        {
          formVals,
        },
        () => {
          if (currentStep < 3) {
            this.forward();
          } else {
            handleUpdate(formVals);
          }
        },
      );
    });
  };

  backward = () => {
    const { currentStep } = this.state;
    this.setState({
      currentStep: currentStep - 1,
    });
  };

  forward = () => {
    const { currentStep } = this.state;
    this.setState({
      currentStep: currentStep + 1,
    });
  };

  goto = (step = 0) => {
    if (step >= 0 && step <= 3) {
      const { form } = this.props;
      const { formVals: oldValue } = this.state;
      form.validateFields((err, fieldsValue) => {
        if (err) return;
        const formVals = { ...oldValue, ...fieldsValue };
        this.setState({ formVals, currentStep: step });
      });
    }
  };

  onSearchService = searchText => {
    const { services } = this.props;
    this.setState({
      serviceDatasource: services
        .filter(item => item.indexOf(searchText) >= 0)
        .map(item => `lb://${item}`),
    });
  };

  renderContent = (currentStep: number, formVals: FormValsType) => {
    const { form } = this.props;
    const { serviceDatasource } = this.state;
    console.log(formVals);
    if (currentStep === 1) {
      return [
        <FormItem key="uri" {...this.formLayout} label="选择服务">
          {form.getFieldDecorator('uri', {
            rules: [{ required: true, message: '请选择匹配路径' }],
            valuePropName: 'defaultValue',
            initialValue: formVals.uri,
          })(
            <AutoComplete
              dataSource={serviceDatasource}
              style={{ width: '100%' }}
              onSearch={this.onSearchService}
            />,
          )}
        </FormItem>,
        <FormItem key="status" {...this.formLayout} label="状态">
          {form.getFieldDecorator('status', {
            rules: [{ required: true, message: '请选择匹配路径' }],
            initialValue: formVals.status,
          })(
            <Select style={{ width: '100%' }}>
              <Option value={0}>未运行</Option>
              <Option value={1}>运行中</Option>
            </Select>,
          )}
        </FormItem>,
      ];
    }
    if (currentStep === 2) {
      return [
        <FormItem key="path" {...this.formLayout} label="Path">
          {form.getFieldDecorator('path', {
            rules: [{ required: true, message: '请选择匹配路径' }],
            initialValue: formVals.path,
          })(<Input placeholder="请输入" />)}
        </FormItem>,
        <FormItem key="order" {...this.formLayout} label="Order">
          {form.getFieldDecorator('order', {
            rules: [{ required: true, message: '请输入排序' }],
            initialValue: formVals.order || 0,
          })(<InputNumber placeholder="请输入" />)}
        </FormItem>,
      ];
    }
    if (currentStep === 3) {
      return [
        <FormItem key="stripPrefix" {...this.formLayout} label="StripPrefix">
          {form.getFieldDecorator('stripPrefix', {
            initialValue: formVals.stripPrefix,
          })(<InputNumber placeholder="请输入" />)}
        </FormItem>,
        <FormItem key="authentication" {...this.formLayout} label="Authentication">
          {form.getFieldDecorator('authentication', {
            valuePropName: 'checked',
            initialValue: formVals.authentication,
          })(<Checkbox>用户认证</Checkbox>)}
        </FormItem>,
        <FormItem key="authorization" {...this.formLayout} label="Authorization">
          {form.getFieldDecorator('authorization', {
            valuePropName: 'checked',
            initialValue: formVals.authorization,
          })(<Checkbox disabled>用户授权</Checkbox>)}
        </FormItem>,
        <FormItem key="loginFacade" {...this.formLayout} label="Authorization">
          {form.getFieldDecorator('loginFacade', {
            valuePropName: 'checked',
            initialValue: formVals.loginFacade,
          })(<Checkbox>登入接口</Checkbox>)}
        </FormItem>,
      ];
    }
    return [
      <FormItem key="name" {...this.formLayout} label="路由ID">
        {form.getFieldDecorator('id', {
          rules: [{ required: true, message: '请输入路由ID！' }],
          initialValue: formVals.id,
        })(
          <Input disabled={formVals.id != undefined && formVals.id !== ''} placeholder="请输入" />,
        )}
      </FormItem>,
      <FormItem key="desc" {...this.formLayout} label="路由描述">
        {form.getFieldDecorator('desc', {
          rules: [{ required: true, message: '请输入至少五个字符的路由描述！', min: 5 }],
          initialValue: formVals.desc,
        })(<TextArea rows={4} placeholder="请输入至少五个字符" />)}
      </FormItem>,
    ];
  };

  renderFooter = (currentStep: number) => {
    const { handleUpdateModalVisible, values } = this.props;
    if (currentStep === 1 || currentStep === 2) {
      return [
        <Button key="back" style={{ float: 'left' }} onClick={this.backward}>
          上一步
        </Button>,
        <Button key="cancel" onClick={() => handleUpdateModalVisible(false, values)}>
          取消
        </Button>,
        <Button key="forward" type="primary" onClick={() => this.handleNext(currentStep)}>
          下一步
        </Button>,
      ];
    }
    if (currentStep === 3) {
      return [
        <Button key="back" style={{ float: 'left' }} onClick={this.backward}>
          上一步
        </Button>,
        <Button key="cancel" onClick={() => handleUpdateModalVisible(false, values)}>
          取消
        </Button>,
        <Button key="submit" type="primary" onClick={() => this.handleNext(currentStep)}>
          完成
        </Button>,
      ];
    }
    return [
      <Button key="cancel" onClick={() => handleUpdateModalVisible(false, values)}>
        取消
      </Button>,
      <Button key="forward" type="primary" onClick={() => this.handleNext(currentStep)}>
        下一步
      </Button>,
    ];
  };

  render() {
    const { updateModalVisible, handleUpdateModalVisible, values } = this.props;
    const { currentStep, formVals } = this.state;

    return (
      <Modal
        width={640}
        bodyStyle={{ padding: '32px 40px 48px' }}
        destroyOnClose
        title="路由配置"
        visible={updateModalVisible}
        footer={this.renderFooter(currentStep)}
        onCancel={() => handleUpdateModalVisible(false, values)}
        afterClose={() => handleUpdateModalVisible()}
      >
        <Steps style={{ marginBottom: 28 }} size="small" current={currentStep} onChange={this.goto}>
          <Step title="基本信息" />
          <Step title="服务信息" />
          <Step title="路由前置" />
          <Step title="路由后置" />
        </Steps>
        {this.renderContent(currentStep, formVals)}
      </Modal>
    );
  }
}

export default Form.create<UpdateFormProps>()(UpdateForm);
