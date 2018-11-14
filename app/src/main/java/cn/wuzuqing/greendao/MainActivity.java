package cn.wuzuqing.greendao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.wuzuqing.greendao.bean.School;
import cn.wuzuqing.greendao.dao.DbCodeManager;
import cn.wuzuqing.greendao.utils.LogUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv;
    private android.widget.Button btn;
    private android.widget.Button gdAdd;
    private android.widget.EditText etcount;
    private Button loadAll;
    private Button delete;
    private Button deleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loadAll = (Button) findViewById(R.id.loadAll);
        this.etcount = (EditText) findViewById(R.id.et_count);
        this.btn = (Button) findViewById(R.id.btn);
        this.gdAdd = (Button) findViewById(R.id.gdAdd);
        this.tv = (TextView) findViewById(R.id.tv);
        this.delete =  findViewById(R.id.delete);
        this.deleteAll =  findViewById(R.id.deleteAll);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long start = System.currentTimeMillis();
                long delete = DbCodeManager.getInstance().getSession().getSchoolDao().delete(4L);
                long endTime = System.currentTimeMillis();
                LogUtils.logd("deleteTime = " + (endTime - start) + " count:" + delete);
            }
        });
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long start = System.currentTimeMillis();
                long delete = DbCodeManager.getInstance().getSession().getSchoolDao().deleteAll();
                long endTime = System.currentTimeMillis();
                LogUtils.logd("deleteAllTime = " + (endTime - start) + " count:" + delete);
            }
        });
        loadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAll();
            }
        });
        btn.setOnClickListener(this);
        gdAdd.setOnClickListener(this);

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();

            }
        });
    }

    private void update() {
        long start = System.currentTimeMillis();
        List<cn.wuzuqing.greendao.bean.School> schools = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            cn.wuzuqing.greendao.bean.School school = new cn.wuzuqing.greendao.bean.School();
            school.setId(i);
            school.setName("二中"+i);
            school.setAddress("很远很远");
            school.setSex((short) i);
            schools.add(school);
        }

        long update = DbCodeManager.getInstance().getSession().getSchoolDao().updateIx(schools);
        long endTime = System.currentTimeMillis();
        LogUtils.logd("updateTime = " + (endTime - start) + " count:" + update);

    }

    private void loadAll() {
        long start = System.currentTimeMillis();
        List<cn.wuzuqing.greendao.bean.School> list = DbCodeManager.getInstance().getSession().getSchoolDao().loadAll();

        long endTime = System.currentTimeMillis();
        LogUtils.logd("loadAll custom Time =  = " + (endTime - start) + " / size = " + list);
    }


    List<cn.wuzuqing.greendao.bean.School> list = new ArrayList<>();

    @Override
    public void onClick(View v) {
        maxSize = Integer.parseInt(etcount.getText().toString());
        long start = System.currentTimeMillis();
        long insertCount = 0;
        switch (v.getId()) {
            case R.id.btn:
                insertCount = custom();
                break;
            case R.id.gdAdd:
                greedDao();
                break;
            default:
        }
        long addTime = System.currentTimeMillis();
        LogUtils.logd("createTime = " + (createTime - start) + " finish Time = " + (addTime - createTime) + " insertCount:" + insertCount);
    }

    private int maxSize = 2000;


    private long custom() {
        list.clear();
        for (int i = 1; i <= maxSize; i++) {
            cn.wuzuqing.greendao.bean.School userInfo = new cn.wuzuqing.greendao.bean.School();
            userInfo.setName("一中");
            userInfo.setSex((short) 10);
            list.add(userInfo);
        }
        createTime = System.currentTimeMillis();
        return DbCodeManager.getInstance().getSession().getSchoolDao().insertIx(list);
    }

    long createTime;

    private void greedDao() {
        List<School> schools = new ArrayList<>();
        for (int i = 1; i <= maxSize; i++) {
            School school = new School();
            school.setName("一中");
            school.setSex((short) 10);
            schools.add(school);
        }
        createTime = System.currentTimeMillis();
    }
}
