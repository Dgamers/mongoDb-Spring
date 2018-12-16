package mongoDb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.geo.Shape;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import model.Item;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class Test01 {

	 private static final double EARTH_RADIUS = 6378137d;
	
    @Autowired
    private MongoOperations mongoOperations;
    
	   @Test
	    public void save() {
/*		   List<Item>items=new ArrayList<>();
		   Item itemA=new Item();
		   itemA.setName("A");
		   itemA.setNumber("123");
		   items.add(itemA);
		   Item itemB=new Item();
		   itemB.setName("B");
		   itemB.setNumber("345");
		   items.add(itemB);
		   mongoOperations.insertAll(items);*/
		   
		   Item item=new Item();
		   item.setName("C");
		   item.setNumber("123");
		   GeoJsonPoint point=new GeoJsonPoint(114.44353, 23.43535);
		   item.setPoint(point);
		   mongoOperations.insert(item);
	    }
	   
	   @Test
	    public void findAll() {
		   List<Item> findAll = mongoOperations.findAll(Item.class);
		   for (Item item : findAll) {
			System.out.println(item);
		}
		   
	    }
	   
	   @Test
	    public void find() {
		   Criteria criteria=Criteria.where("name").exists(true);
	       Query query=new Query(criteria);
		   List<Item> findAll = mongoOperations.find(query, Item.class);
		   for (Item item : findAll) {
			System.out.println(item);
		}
		   
	    }
	   
/*	   @Test
	    public void delete() {
		 
		   mongoOperations.dropCollection("item");
	    }*/
	   
	   @Test
	    public void update() {
		   Criteria criteria=Criteria.where("name").is("A");
	       Query query=new Query(criteria);
	       Update update=new Update();
	       update.set("name", "newA");
		   mongoOperations.updateFirst(query, update, Item.class);
	    }
	   
	   
	   /**
	     * $near 查询附近位置
	     * db.runCommand({
	     *    geoNear: "driverPoint" ,
	     *    near: {"lng":118.193828,"lat":24.492242} ,
	     *    spherical: true,
	     *    maxDistance:10/3963.2,
	     *    distanceMultiplier:3963.2,
	     *    query:{"driverUuid":"d43bf3d759064ed69a914cb10a010e77"}
	     * })
	     */
	    @Test
	    public void nearTest() {
	        NearQuery nearQuery = NearQuery.near(114.44353, 23.43535)
	                .distanceMultiplier(EARTH_RADIUS)
	                .maxDistance(3000)
	                .spherical(true);
	        GeoResults<Item> geoResults = mongoOperations.geoNear(nearQuery, Item.class);
	        List<GeoResult<Item>> content = geoResults.getContent();
	        System.out.println(content);

	    }

	    /*nearSphere*/
	    @Test
	    public void nearSphereTest(){
	        Point x = new Point(114.44353, 23.43535);
	        Criteria criteria = Criteria.where("point").nearSphere(x);
	        criteria.maxDistance(3000 / EARTH_RADIUS);
	        Query query = new Query(criteria);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
				System.out.println(item);
			}
	    }


	    /*within*/

	    /**
	     * "$geoWithin"操作符找出完全包含在某个区域的文档？
	     * db.driverPoint.find(
	     *    {
	     *      coordinate: {
	     *        $geoIntersects: {
	     *           $geometry: {
	     *              type: "Polygon" ,
	     *              coordinates: [
	     *                [ [ 118.193828, 24.492242 ], [ 118.193953, 24.702114 ], [ 118.19387, 24.592242 ],[ 118.193828, 24.492242 ]]
	     *              ]
	     *           }
	     *        }
	     *      }
	     *    }
	     * )
	     */
	    @Test
	    public void withInGeoJsonPolygonTest() {
	        Point x = new Point(114.44353, 23.43535);
	        Point y = new Point(118.193953, 24.702114);
	        Point z = new Point(119.19387, 28.792242);
	        Shape shape = new GeoJsonPolygon(x, y, z, x);
	        Criteria criteria = Criteria.where("point").within(shape);
	        Query query = new Query(criteria);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
				System.out.println(item);
			}
	    }


	    /**
	     * "$geoWithin"操作符找出矩形范围内的文档？
	     * db.driverPoint.find(
	     * {
	     *   coordinate: {
	     *      $geoWithin: {
	     *         $box: [
	     *           [ 118.0,24.0 ],
	     *           [ 120.0,30.0 ]
	     *         ]
	     *      }
	     *   }
	     * }
	     * )
	     */
	    @Test
	    public void withInBoxTest() {
	        Point x = new Point(114.44353, 23.43535);
	        Point y = new Point(120.0, 30.0);
	        Shape shape = new Box(x, y);
	        Criteria coordinate = Criteria.where("point").within(shape);
	        Query query = new Query(coordinate);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
					System.out.println(item);
				}
	    }


	    /**
	     * $geoWithin"操作符找出圆形范围内的文档？
	     * db.driverPoint.find(
	     * {
	     *   coordinate: {
	     *      $geoWithin: {
	     *          $center: [ [ 118.067678, 24.444373] , 10 ]
	     *      }
	     *   }
	     * }
	     * )
	     */
	    @Test
	    public void withInCircleTest() {
	        Point point = new Point(114.44353, 23.43535);
	        Distance distance = new Distance(10, Metrics.KILOMETERS);
	        Shape shape = new Circle(point, distance);
	        Criteria criteria = Criteria.where("point").within(shape);
	        Query query = new Query(criteria);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
					System.out.println(item);
				}
	    }

	    /**
	     * "$geoWithin"操作符找出多边形范围内的文档？
	     * db.driverPoint.find(
	     * {
	     *   coordinate: {
	     *      $geoWithin: {
	     *          $polygon: [ [ 118.067678 , 24.444373 ], [ 119.067678 , 25.444373 ], [ 120.067678 , 26.444373 ] ]
	     *      }
	     *   }
	     * }
	     * )
	     */
	    @Test
	    public void withInPolygonTest() {
	        Point x = new Point(114.44353, 23.43535);
	        Point y = new Point(118.193953, 24.702114);
	        Point z = new Point(119.19387, 28.792242);
	        Shape shape = new Polygon(x, y ,z);
	        Criteria coordinate = Criteria.where("point").within(shape);
	        Query query = new Query(coordinate);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
					System.out.println(item);
				}
	    }


	    /**
	     * $geoWithin"操作符找出球面圆形范围内的文档？
	     *
	     * db.driverPoint.find(
	     * {
	     *   coordinate: {
	     *      $geoWithin: {
	     *          $centerSphere: [ [ 118.067678, 24.444373 ], 10/3963.2 ]
	     *      }
	     *   }
	     * }
	     * )
	     */
	    @Test
	    public void withInCenterSphereTest(){
	        Circle circle = new Circle(114.44353, 23.43535 , 10/3963.2);
	        Criteria criteria = Criteria.where("point").withinSphere(circle);
	        Query query = new Query(criteria);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
					System.out.println(item);
				}
	    }

	    /*Intersects*/

	    /**
	     * "$geoIntersects" 操作符找出与查询位置相交的文档 ？
	     * db.driverPoint.find(
	     *    {
	     *      coordinate: {
	     *        $geoIntersects: {
	     *           $geometry: {
	     *              type: "Polygon" ,
	     *              coordinates: [
	     *                [ [ 118.193828, 24.492242 ], [ 118.193953, 24.702114 ], [ 118.19387, 24.592242 ],[ 118.193828, 24.492242 ]]
	     *              ]
	     *           }
	     *        }
	     *      }
	     *    }
	     * )
	     */
	    @Test
	    public void intersectsTest(){
	        Point x = new Point(114.44353, 23.43535);
	        Point y = new Point(118.193953, 24.702114);
	        Point z = new Point(119.19387, 28.792242);
	        GeoJson geoJson = new  GeoJsonPolygon(x, y, z, x);
	        Criteria coordinate = Criteria.where("point").intersects(geoJson);
	        Query query = new Query(coordinate);
	        List<Item> objects = mongoOperations.find(query, Item.class);
	        for (Item item : objects) {
					System.out.println(item);
				}
	    }

	   
}
