package com.coba;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uk.co.flax.luwak.*;
import uk.co.flax.luwak.matchers.SimpleMatcher;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.queryparsers.LuceneQueryParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class luwakSearchInputFile {
    public static void main(String[] args) throws IOException, ParseException {
        final AtomicInteger purgeCount = new AtomicInteger();
        QueryIndexUpdateListener listener = new QueryIndexUpdateListener() {
            @Override
            public void onPurge() {
                purgeCount.incrementAndGet();
            }
        };

        Monitor monitor = new Monitor(new LuceneQueryParser("field"), new TermFilteredPresearcher());

        //=============== from json ==============
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("/home/fajar/Documents/kerja/luwakSearch/src/main/java/com/coba/location.json");
        //Read JSON file
        Object obj = jsonParser.parse(reader);

        JSONArray jsonList = (JSONArray) obj;

        MonitorQuery[] mq = new MonitorQuery[jsonList.size()];
        int cek = 0;
        for (Object data : jsonList){
            JSONObject jsonObject = new JSONObject((Map) data);
            String name = (String) jsonObject.get("name");
            String query = (String) jsonObject.get("query");
            mq[cek] = new MonitorQuery(name,query);
            cek++;
        }

        //=============== from csv ==============
//        String line = "";
//        List<String> listQuery = new ArrayList<>();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader("/home/fajar/Documents/kerja/luwakSearch/src/main/java/com/coba/data.csv"));
//            while ((line = br.readLine()) != null) {
//               listQuery.add(line);
//            }
//        }catch (Exception e){
//            System.out.println(e);
//        }
//        MonitorQuery[] mq = new MonitorQuery[listQuery.size()];
//        int cek =0;
//        for (String data : listQuery){
////            System.out.println(data);
//          String[] set = data.split(",");
//          mq[cek] = new MonitorQuery(set[0],"\""+set[1]+"\"");
//          cek++;
//        }

//        monitor.addQueryIndexUpdateListener(listener);


        monitor.update(mq);


//      match one document at a time
        InputDocument doc = InputDocument.builder("doc1")
                .addField("field", "123kota pelajar banjir coy pulau dewata", new StandardAnalyzer())
                .addField("field", "kota batik kering bray kota hujan", new StandardAnalyzer())
                .build();

        Matches<QueryMatch> matches = monitor.match(doc, SimpleMatcher.FACTORY);


        for (DocumentMatches<QueryMatch> test : matches){
            for (QueryMatch test2 : test){
                System.out.println(test2.getQueryId());
                System.out.println(monitor.getQuery(test2.getQueryId()));
                System.out.println(doc.getDocument().getField("field").stringValue());
            }
        }

    }

}
