package com.coba;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.json.simple.parser.ParseException;
import uk.co.flax.luwak.*;
import uk.co.flax.luwak.matchers.SimpleMatcher;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.queryparsers.LuceneQueryParser;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class luwakSearchNoTlp {
    public static void main(String[] args) throws IOException, ParseException {
        final AtomicInteger purgeCount = new AtomicInteger();

        QueryIndexUpdateListener listener = new QueryIndexUpdateListener() {
            @Override
            public void onPurge() {
                purgeCount.incrementAndGet();
            }
        };

        //input number in String
        String text = "081112912342399";

        Monitor monitor = new Monitor(new LuceneQueryParser("field"), new TermFilteredPresearcher());

        MonitorQuery[] mq = new MonitorQuery[]{
                new MonitorQuery("bogor","081110"),
                new MonitorQuery("bandung","0811129"),
                new MonitorQuery("solo","08112"),
                new MonitorQuery("bali","\"pulau dewata\"")
        };


        monitor.update(mq);

        MonitorQuery result = null;
        if (cekMatches(text,monitor) == null){
            for (int i = 5; i < text.length();i++){
                String a = text.substring(0,i);
                MonitorQuery q = cekMatches(a,monitor);
                if (q != null){
                    result = q;
                }
            }
        }
        System.out.println(result);

    }

    private static MonitorQuery cekMatches(String a, Monitor monitor) throws IOException {
        InputDocument doc = InputDocument.builder("doc1")
                .addField("field", a, new StandardAnalyzer())
                .addField("field", "kota batik kering bray kota hujan", new StandardAnalyzer())
                .build();

        Matches<QueryMatch> matches = monitor.match(doc, SimpleMatcher.FACTORY);

        MonitorQuery data = null;
        for (DocumentMatches<QueryMatch> test : matches){
            for (QueryMatch test2 : test){
//                System.out.println(test2.getQueryId());

                data = monitor.getQuery(test2.getQueryId());

//                System.out.println(doc.getDocument().getField("field").stringValue());
            }
//            System.out.println(Arrays.toString(test.getMatches().toArray()));
        }

//        System.out.println(data);
        return data;
    }



}
