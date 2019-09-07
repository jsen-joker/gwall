import {
  Button,
  Divider,
  Input,
  Popconfirm,
  Table,
  message,
  Select,
  AutoComplete,
  Tag,
} from 'antd';
import React, { Fragment, PureComponent } from 'react';

import isEqual from 'lodash.isequal';
import styles from '../style.less';

interface TableFormDateType {
  type: string;
  value: string;
  editable?: boolean;
  isNew?: boolean;
}
interface TableFormProps {
  loading?: boolean;
  value?: TableFormDateType[];
  onChange?: (value: TableFormDateType[]) => void;
  types?: string[];
}

interface TableFormState {
  loading?: boolean;
  value?: TableFormDateType[];
  data?: TableFormDateType[];
}
class TableForm extends PureComponent<TableFormProps, TableFormState> {
  static getDerivedStateFromProps(nextProps: TableFormProps, preState: TableFormState) {
    if (isEqual(nextProps.value, preState.value)) {
      return null;
    }
    return {
      data: nextProps.value,
      value: nextProps.value,
    };
  }

  clickedCancel: boolean = false;

  index = 0;

  cacheOriginData = {};

  columns = [
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: '20%',
      render: (text: string, record: TableFormDateType, index: number) => {
        if (record.editable) {
          return (
            <Select
              defaultValue={text && text !== '' ? text : undefined}
              placeholder="类型"
              style={{ width: '100%' }}
              onChange={e => this.handleSelectFieldChange(e, 'type', index)}
            >
              {this.props.types &&
                this.props.types.map(item => (
                  <Select.Option value={item} key={item}>
                    {item}
                  </Select.Option>
                ))}
            </Select>
          );
        }
        return <Tag color="green">{text}</Tag>;
      },
    },
    {
      title: '值',
      dataIndex: 'value',
      key: 'value',
      width: '60%',
      render: (text: string, record: TableFormDateType, index: number) => {
        if (record.editable) {
          return (
            <Input
              value={text}
              style={{ width: '100%' }}
              onChange={e => this.handleFieldChange(e, 'value', index)}
              onKeyDown={e => this.handleKeyPress(e, index)}
              placeholder="值"
            />
          );
        }
        return text;
      },
    },
    {
      title: '操作',
      key: 'action',
      render: (text: string, record: TableFormDateType, index: number) => {
        const { loading } = this.state;
        if (!!record.editable && loading) {
          return null;
        }
        if (record.editable) {
          if (record.isNew) {
            return (
              <span>
                <a onClick={e => this.saveRow(e, index)}>添加</a>
                <Divider type="vertical" />
                <Popconfirm title="是否要删除此行？" onConfirm={() => this.remove(index)}>
                  <a>删除</a>
                </Popconfirm>
              </span>
            );
          }
          return (
            <span>
              <a onClick={e => this.saveRow(e, index)}>保存</a>
              <Divider type="vertical" />
              <a onClick={e => this.cancel(e, index)}>取消</a>
            </span>
          );
        }
        return (
          <span>
            <a onClick={e => this.toggleEditable(e, index)}>编辑</a>
            <Divider type="vertical" />
            <Popconfirm title="是否要删除此行？" onConfirm={() => this.remove(index)}>
              <a>删除</a>
            </Popconfirm>
          </span>
        );
      },
    },
  ];

  constructor(props: TableFormProps) {
    super(props);
    this.state = {
      data: props.value,
      loading: false,
      value: props.value,
    };
  }

  getRowByIndex(index: number, newData?: TableFormDateType[]) {
    const { data = [] } = this.state;
    return (newData || data)[index];
  }

  toggleEditable = (e: React.MouseEvent | React.KeyboardEvent, index: number) => {
    e.preventDefault();
    const { data = [] } = this.state;
    const newData = data.map(item => ({ ...item }));
    const target = this.getRowByIndex(index, newData);
    if (target) {
      // 进入编辑状态时保存原始数据
      if (!target.editable) {
        this.cacheOriginData[index] = { ...target };
      }
      target.editable = !target.editable;
      this.setState({ data: newData });
    }
  };

  newMember = () => {
    const { data = [] } = this.state;
    const newData = data.map(item => ({ ...item }));
    newData.push({
      type: '',
      value: '',
      editable: true,
      isNew: true,
    });
    this.index += 1;
    this.setState({ data: newData });
  };

  remove(index: number) {
    const { data = [] } = this.state;
    const { onChange } = this.props;
    const newData = [...data];
    newData.splice(index, 1);
    this.setState({ data: newData });
    if (onChange) {
      onChange(newData);
    }
  }

  handleKeyPress(e: React.KeyboardEvent, index: number) {
    if (e.key === 'Enter') {
      this.saveRow(e, index);
    }
  }

  handleFieldChange(e: React.ChangeEvent<HTMLInputElement>, fieldName: string, index: number) {
    const { data = [] } = this.state;
    const newData = [...data];
    const target = this.getRowByIndex(index, newData);
    if (target) {
      target[fieldName] = e.target.value;
      this.setState({ data: newData });
    }
  }

  handleSelectFieldChange(value: string, fieldName: string, index: number) {
    const { data = [] } = this.state;
    const newData = [...data];
    const target = this.getRowByIndex(index, newData);
    if (target) {
      target[fieldName] = value;
      this.setState({ data: newData });
    }
  }

  saveRow(e: React.MouseEvent | React.KeyboardEvent, index: number) {
    e.persist();
    this.setState({
      loading: true,
    });
    setTimeout(() => {
      if (this.clickedCancel) {
        this.clickedCancel = false;
        return;
      }
      const target = this.getRowByIndex(index) || {};
      console.log(target);
      if (!target.type) {
        message.error('请填写完整信息。');
        (e.target as HTMLInputElement).focus();
        this.setState({
          loading: false,
        });
        return;
      }
      delete target.isNew;
      this.toggleEditable(e, index);
      const { data = [] } = this.state;
      const { onChange } = this.props;
      if (onChange) {
        onChange(data);
      }
      this.setState({
        loading: false,
      });
    }, 100);
  }

  cancel(e: React.MouseEvent, index: number) {
    this.clickedCancel = true;
    e.preventDefault();
    const { data = [] } = this.state;
    const newData = [...data];
    // 编辑前的原始数据
    let cacheOriginData = [];
    cacheOriginData = newData.map((item, i) => {
      if (index === i) {
        if (this.cacheOriginData[index]) {
          const originItem = {
            ...item,
            ...this.cacheOriginData[index],
            editable: false,
          };
          delete this.cacheOriginData[index];
          return originItem;
        }
      }
      return item;
    });

    this.setState({ data: cacheOriginData });
    this.clickedCancel = false;
  }

  render() {
    const { loading, data } = this.state;

    return (
      <Fragment>
        <Table<TableFormDateType>
          loading={loading}
          columns={this.columns}
          dataSource={data}
          pagination={false}
          rowClassName={record => (record.editable ? styles.editable : '')}
        />
        <Button
          style={{ width: '100%', marginTop: 16, marginBottom: 8 }}
          type="dashed"
          onClick={this.newMember}
          icon="plus"
        >
          增加
        </Button>
      </Fragment>
    );
  }
}

export default TableForm;
