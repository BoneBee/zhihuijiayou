package com.ncse.zhhygis.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ncse.zhhygis.collect.SpaceDatasource;
import com.ncse.zhhygis.mapper.MaplogInfosMapper;
import com.ncse.zhhygis.service.RoutePlanService;
import com.ncse.zhhygis.utils.projectUtils.GeoRegionUtil;
import com.supermap.analyst.networkanalyst.PathGuide;
import com.supermap.analyst.networkanalyst.PathGuideItem;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoPoint;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Point2Ds;
import com.supermap.data.PointM;
import com.supermap.data.PointMs;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;

@Service
public class RoutePlanServiceImpl implements RoutePlanService{
	private final Logger log = Logger.getLogger(this.getClass());

	@Value("${routeNetName}")
	private String routeNetName;

	@Value("${routeNetName}")
	private String barrierName;

	/*private Workspace workspace;

	private Datasource datasource;*/

	@Value("${supermap.server}")
	private String server;
	/*@Value("${supermap.database}")
	private String database;*/
	@Value("${supermap.user}")
	private String user;
	@Value("${supermap.password}")
	private String password;

	private DatasetVector datasetVector;

	private DatasetVector barrierDV;

	/*private DatasourceConnectionInfo datasourceconnection;*/

	private static String m_nodeID = "SmNodeID";

	private static String m_edgeID = "SmEdgeID";

	private TransportationAnalyst m_analyst;

	ArrayList<Integer> m_barrierNodes;
	ArrayList<Integer> m_barrierEdges;

	private TransportationAnalystResult m_result;

	private Map returnMap;
	
	@Autowired
	private MaplogInfosMapper maplogInfosMapper;

	/*public void setDatasourceconnection(String aircode) {
		if (datasourceconnection != null) {
			datasourceconnection.dispose();
		}
		datasourceconnection = new DatasourceConnectionInfo();
		datasourceconnection.setEngineType(EngineType.MYSQL);
		datasourceconnection.setServer(server);
		datasourceconnection.setDatabase(maplogInfosMapper.queryDatabase(aircode));
		datasourceconnection.setUser(user);
		datasourceconnection.setPassword(password);
		datasourceconnection.setAlias(user);
	}*/

	/*public void closeWorkspace() {
		closeDatasources();
		this.workspace.close();
		this.workspace.dispose();
	}

	public void closeDatasources() {
		this.workspace.getDatasources().closeAll();
	}*/

	public Map findPath(Map map) {
		String aircode=(String)map.get("aircode");
		m_barrierNodes = new ArrayList<Integer>();
		m_barrierEdges = new ArrayList<Integer>();
		returnMap = new HashMap();
		/*workspace = new Workspace();
		setDatasourceconnection(aircode);
		datasource = workspace.getDatasources().open(datasourceconnection);*/
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		datasetVector = (DatasetVector) datasource.getDatasets().get(routeNetName);
		DatasetVector datasetVector = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),routeNetName);
		load();
		analyst(map);
		m_analyst.dispose();
		//datasetVector.close();
		/*datasource.close();
		datasourceconnection.dispose();
		workspace.dispose();*/
		return returnMap;
	}

	public void load() {
		try {
			// 设置网络分析基本环境，这一步骤需要设置 分析权重、节点、弧段标识字段、容限
			TransportationAnalystSetting setting = new TransportationAnalystSetting();
			setting.setNetworkDataset(datasetVector);
			setting.setEdgeIDField(m_edgeID);
			setting.setNodeIDField(m_nodeID);
			setting.setEdgeNameField("Name");
			setting.setTolerance(0.001);

			WeightFieldInfos weightFieldInfos = new WeightFieldInfos();
			WeightFieldInfo weightFieldInfo = new WeightFieldInfo();
			weightFieldInfo.setFTWeightField("SmLength");
			weightFieldInfo.setTFWeightField("SmLength");
			weightFieldInfo.setName("length");
			weightFieldInfos.add(weightFieldInfo);
			setting.setWeightFieldInfos(weightFieldInfos);
			setting.setFNodeIDField("SmFNode");
			setting.setTNodeIDField("SmTNode");

			// 构造交通网络分析对象，加载环境设置对象
			m_analyst = new TransportationAnalyst();
			m_analyst.setAnalystSetting(setting);
			m_analyst.load();
		} catch (Exception e) {
			log.info(e.getMessage());
			returnMap.put("msg", e.getMessage());
		}
	}

	/**
	 * 设置路障
	 */
	public void setBarrier(String aircode) {
//		Datasource datasource =SpaceDatasource.mysqlDatasources.get(maplogInfosMapper.queryDatabase(aircode));
//		barrierDV = (DatasetVector) datasource.getDatasets().get(barrierName);
		DatasetVector barrierDV = SpaceDatasource.getDataVector(maplogInfosMapper.queryDatabase(aircode),barrierName);
		Recordset recordset = barrierDV.getRecordset(false, CursorType.STATIC);
		Geometry geometry;

		for (int i = 0; i < recordset.getRecordCount(); i++) {
			geometry = recordset.getGeometry();
			// 捕捉到点时，将捕捉到的点添加到障碍点列表中
			if (geometry.getType() == GeometryType.GEOPOINT) {
				GeoPoint geoPoint = (GeoPoint) geometry;
				int nodeID = recordset.getInt32("SMNODEID");
				m_barrierNodes.add(nodeID);
			}
			// 捕捉到线时，将线对象添加到障碍线列表中
			if (geometry.getType() == GeometryType.GEOLINE) {
				GeoLine geoLine = (GeoLine) geometry;
				int edgeID = recordset.getInt32("SMEDGEID");
				m_barrierEdges.add(edgeID);
			}
			recordset.moveNext();
		}
		recordset.dispose();
		//recordset.close();
		//barrierDV.close();
	}

	/**
	 * 进行最短路径分析
	 */
	public boolean analyst(Map map) {
		//try {
			// m_count = 0;
			TransportationAnalystParameter parameter = new TransportationAnalystParameter();
			// 设置障碍点及障碍边
			//setBarrier((String)map.get("aircode"));//设置路障
			int[] barrierEdges = new int[m_barrierEdges.size()];
			for (int i = 0; i < barrierEdges.length; i++) {
				barrierEdges[i] = m_barrierEdges.get(i);
			}
			parameter.setBarrierEdges(barrierEdges);

			int[] barrierNodes = new int[m_barrierNodes.size()];
			for (int i = 0; i < barrierNodes.length; i++) {
				barrierNodes[i] = m_barrierNodes.get(i);
			}
			parameter.setBarrierNodes(barrierNodes);
			parameter.setWeightName("length");

			// 设置最佳路径分析的返回对象
			String points = (String) map.get("points");
			Point2Ds point2ds = GeoRegionUtil.getPoint2ds(points);
			parameter.setPoints(point2ds);
			parameter.setNodesReturn(true);
			parameter.setEdgesReturn(true);
			parameter.setPathGuidesReturn(true);
			parameter.setRoutesReturn(true);
			
			// 进行分析并显示结果
			m_result = m_analyst.findPath(parameter, false);
			if (m_result == null) {
				returnMap.put("msg", "路径解析失败");
				return false;
			}
			showResult();
			
			return true;
		/*} catch (Exception e) {
			log.info(e.getMessage());
			returnMap.put("msg", e.getMessage());
			return false;
		}*/
	}

	/**
	 * 显示结果
	 */
	public void showResult() {
		
		try {
			fillResultTable(0);
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < m_result.getRoutes().length; i++) {
				GeoLineM geoLineM = m_result.getRoutes()[0];
				for(int x=0;x<geoLineM.getPartCount();x++) {
					PointMs pointms=geoLineM.getPart(x);
					for(int y=0;y<pointms.getCount();y++) {
						PointM pointM=pointms.getItem(y);
						sb.append(pointM.getX()).append(",").append(pointM.getY()).append(";");
					}
				}
			}
			returnMap.put("points", sb.toString());
		} catch (Exception e) {
			log.info(e.getMessage());
			returnMap.put("msg", e.getMessage());
		}
	}

	/**
	 * 对结果表进行填充
	 */
	public  void fillResultTable(int pathNum) {
		try {
			// 清除原数据，添加初始点信息
			

			// 得到行驶导引对象，根据导引子项类型的不同进行不同的填充
			PathGuide[] pathGuides = m_result.getPathGuides();
			PathGuide pathGuide = pathGuides[pathNum];
			double distanct=0.0;
			for (int j = 1; j < pathGuide.getCount(); j++) {
				PathGuideItem item = pathGuide.get(j);
			
				// 导引子项为站点的添加方式
				if (item.isStop()) {
					distanct +=item.getDistance();					
				}
				// 导引子项为弧段的添加方式
				if (item.isEdge()) {
					distanct +=item.getLength();	
				}
			}
			//根据路径规划，返回距离。
			returnMap.put("totalDistance", distanct);
		} catch (Exception e) {
			log.info(e.getMessage());
			returnMap.put("msg", e.getMessage());
		}
	}

}
