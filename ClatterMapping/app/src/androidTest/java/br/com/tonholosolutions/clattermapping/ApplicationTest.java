package br.com.tonholosolutions.clattermapping;

import android.test.ApplicationTestCase;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;

import br.com.tonholosolutions.clattermapping.model.Mapping;
import br.com.tonholosolutions.clattermapping.model.Mapping$Table;
import br.com.tonholosolutions.clattermapping.orm.ClatterApplication;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<ClatterApplication> {
    public ApplicationTest() {
        super(ClatterApplication.class);
    }

    public void testMock() {
        Delete.table(Mapping.class, Condition.column(Mapping$Table.DECIBEL).lessThan(0));

        Mapping map = new Mapping(-19.8976549, -43.9409383, 51.4741492262203);
        map.save();
        map = new Mapping(-19.8972121, -43.9405284, 55.6414113554172);
        map.save();
        map = new Mapping(-19.9048658, -43.9361198, 52.6414113554172);
        map.save();
        map = new Mapping(-19.9053875, -43.9370564, 67.5268471973352);
        map.save();
        map = new Mapping(-19.9065643, -43.9377698, 95.577976588322);
        map.save();
        map = new Mapping(-19.9105709, -43.9388196, 60.5268471973352);
        map.save();
        map = new Mapping(-19.9135609, -43.9443818, 52.8903489081592);
        map.save();
        map = new Mapping(-19.9149724, -43.9537416, 58.8902489081592);
        map.save();
        map = new Mapping(-19.9162186, -43.9527158, 51.4741492262203);
        map.save();
        map = new Mapping(-19.9196143, -43.9471601, 55.3214567321456);
        map.save();
        map = new Mapping(-19.9220903, -43.9456585, 52.3452112456785);
        map.save();
        map = new Mapping(-19.9231144, -43.9459322, 61.4231471973352);
        map.save();
        map = new Mapping(-19.9234899, -43.9448123, 58.9877659973352);
        map.save();
        map = new Mapping(-19.9329215, -43.938857, 51.4741492262203);
        map.save();
        map = new Mapping(-19.9331583, -43.9381121, 51.5432492262203);
        map.save();
        map = new Mapping(-19.9341891, -43.93807570000001, 40.474149226226);
        map.save();
    }
}