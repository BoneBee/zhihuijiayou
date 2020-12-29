package com.ncse.zhhygis.serviceImpl;

import com.ncse.zhhygis.collect.SpaceDatasource;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.MapOilWellService;
import com.supermap.data.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ClassName:  [功能名称]
 * Description:  [油井信息]
 * Date:  2018/11/26 10 56
 *
 * @author Xugn
 * @version 1.0.0
 */
@Service
public class MapOilWellServiceImpl implements MapOilWellService {

    private final Logger log = Logger.getLogger(this.getClass());

    @Value("${supermap.server}")
    private String server;
    @Value("${supermap.user}")
    private String user;
    @Value("${supermap.password}")
    private String password;
    @Value("${draw.poilwell}")
    private String poilwell;

    @Autowired
    private MaplogInfosMapper maplogInfosMapper;


    @Override
    public List getOilWell(String aircode,String aircraft) {
        List list=new ArrayList<>();
        //code保留，应该不同的数据库或者不同数据集，待以后修改
        /*Workspace workspace=new Workspace();
        //Datasources datasources=workspace.getDatasources();
        DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
        datasourceconnection.setEngineType(EngineType.MYSQL);
        datasourceconnection.setServer(server);
        datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
        datasourceconnection.setUser(user);
        datasourceconnection.setPassword(password);
        datasourceconnection.setAlias(user);
        // 打开数据源
        Datasource datasource = workspace.getDatasources().open(datasourceconnection);*/
//        Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//        DatasetVector datasetVector = (DatasetVector) datasource.getDatasets().get(poilwell);
        //判断数据集是否为空
        DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),poilwell);
        if (datasetVector == null) {
            /*datasetVector.close();
            datasource.close();
            datasourceconnection.dispose();
            workspace.dispose();*/
            return null;
        }
        //取所有的数据集的数据
        Recordset prs = datasetVector.query("Name LIKE'" + aircraft + "%'", CursorType.STATIC);
        for(int i=0;i<prs.getRecordCount();i++) {//获取游标总条数进行for循环
            String name=prs.getString("Name");
            list.add(name);
            prs.moveNext();
        }
        prs.dispose();
//        prs.close();
//        datasetVector.close();
      /*  datasource.close();
        datasourceconnection.dispose();
        workspace.dispose();*/
        return list;
    }
}
