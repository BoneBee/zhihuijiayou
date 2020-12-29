package com.ncse.zhhygis.collect;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Workspace;


@Component
public class SpaceDatasource extends Datasource{
	
	@Value("${supermap.server}")
	private String server;
	@Value("${supermap.user}")
	private String user;
	@Value("${supermap.password}")
	private String password;
	
	/*@Value("${draw.functinalRegionName}")
	private String functinalRegion;*/
	
	@Value("${draw.managerRegionName}")
	private String managerRegionName;

	@Value("${draw.functinalRegionName}")
	private String functinalRegionName;

	@Value("${draw.aParkRegionName}")
	private String aParkRegionName;


	@Value("${draw.pAircraftName}")
	private String pAirName;

    @Value("${draw.poilwell}")
    private String poilwell;
    
    @Value("${routeNetName}")
	private String routeNetName;

    @Value("${draw.oilLine}")
    private String oilLine;
	
	@Autowired
    private MaplogInfosMapper maplogInfosMapper;
	
	public static Map<String ,Datasource> mysqlDatasources=new HashMap();
	public static Map<String ,Map<String,DatasetVector>> mysqlDataVector=new HashMap();

	/*@Bean(name="mysqlDatasource")
	public Datasource mysqlDatasource(@Qualifier("myWorkspace") Workspace workspace) {
		DatasourceConnectionInfo datasourceconnection2 = new DatasourceConnectionInfo();
		datasourceconnection2.setEngineType(EngineType.MYSQL);
		datasourceconnection2.setServer(server);
		datasourceconnection2.setDatabase(k);
		datasourceconnection2.setUser(user);
		datasourceconnection2.setPassword(password);
		datasourceconnection2.setAlias(user);
		Datasource ds2 = workspace.getDatasources().open(datasourceconnection2);
		return ds2;
	}*/
	
	@Bean
	public Datasource zhhydatasource() {
		Workspace workspace = new Workspace();	
		DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MEMORY);
		datasourceconnection.setReadOnly(false);
		Datasource datasourc=workspace.getDatasources().create(datasourceconnection);
		/**************创建内存数据集************************************/
		maplogInfosMapper.queryAllDatabase().forEach(k->{
			DatasourceConnectionInfo datasourceconnection2 = new DatasourceConnectionInfo();
			datasourceconnection2.setEngineType(EngineType.MYSQL);
			datasourceconnection2.setServer(server);
			datasourceconnection2.setDatabase(k);
			datasourceconnection2.setUser(user);
			datasourceconnection2.setPassword(password);
			datasourceconnection2.setAlias(k);
			Datasource ds2 = workspace.getDatasources().open(datasourceconnection2);
			mysqlDatasources.put(k, ds2);
			Map m=new HashMap();
			putDataVector(ds2,managerRegionName,m);
			putDataVector(ds2,aParkRegionName,m);
			putDataVector(ds2,pAirName,m);
			putDataVector(ds2,poilwell,m);
			putDataVector(ds2,routeNetName,m);
			//新增油井线
			putDataVector(ds2,oilLine,m);
			DatasetVector datasetVector = (DatasetVector) ds2.getDatasets().get(functinalRegionName);
			if(datasetVector==null) {
				ds2.close();
				return;
			}else {
				Dataset rs1 =datasourc.copyDataset(datasetVector, k, datasetVector.getEncodeType());
				rs1.close();
				datasetVector.close();
				putDataVector(ds2,functinalRegionName,m);
			}

			mysqlDataVector.put(k, m);

			//ds2.close();
		});
		 /************************************************************/

		return datasourc;
	}
	
	//更新数据
	public static void copydataset(Datasource datasource,String datasetName, DatasetVector datasetVector) {
		datasource.getDatasets().delete(datasetName);
		Dataset rs1 =datasource.copyDataset(datasetVector, datasetName, datasetVector.getEncodeType());
		rs1.close();
		//datasetVector.close();
	}
	
	/*public void mySetBean(String aircode) {
		//将applicationContext转换为ConfigurableApplicationContext
	    ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextUtil.getApplicationContext();

	    // 获取bean工厂并转换为DefaultListableBeanFactory
	    DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

	    // 通过BeanDefinitionBuilder创建bean定义
	    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Datasource.class);
	    // 设置属性userService,此属性引用已经定义的bean:userService,这里userService已经被spring容器管理了.
//	    beanDefinitionBuilder.addPropertyReference("testService", "testService");

	    // 注册bean
	    defaultListableBeanFactory.registerBeanDefinition(aircode+"datasource", beanDefinitionBuilder.getRawBeanDefinition());

	}
	
	
	
	public static Datasource getMysqlDatasource(String aircode) {
		return (Datasource)(SpringContextUtil.getBean(aircode+"datasource"));
	}
*/
	private void putDataVector(Datasource ds2,String name,Map m) {
		//Datasource ds2 = workspace.getDatasources().open(datasourceconnection2);
		//mysqlDatasources.put(k, ds2);
		DatasetVector datasetVector = (DatasetVector) ds2.getDatasets().get(name);
		/*if(datasetVector==null) {
			ds2.close();
			return;
		}*/
		m.put(name, datasetVector);
	}
	
	public static DatasetVector getDataVector(String dataname,String dvname) {
		 return mysqlDataVector.get(dataname).get(dvname);
	}
	
	public static Datasource getDataSource(String dataname) {
		return mysqlDatasources.get(dataname);
	}
	
	public static void putDataVector(DatasetVector datasetVector,String dataname,String dvname) {
		mysqlDataVector.get(dataname).put(dvname, datasetVector);
	}
	
}
