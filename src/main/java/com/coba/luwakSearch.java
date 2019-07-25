package com.coba;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.json.simple.parser.ParseException;
import uk.co.flax.luwak.*;
import uk.co.flax.luwak.matchers.SimpleMatcher;
import uk.co.flax.luwak.presearcher.TermFilteredPresearcher;
import uk.co.flax.luwak.queryparsers.LuceneQueryParser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class luwakSearch {
    public static void main(String[] args) throws IOException, ParseException {
        final AtomicInteger purgeCount = new AtomicInteger();
        QueryIndexUpdateListener listener = new QueryIndexUpdateListener() {
            @Override
            public void onPurge() {
                purgeCount.incrementAndGet();
            }
        };

        Monitor monitor = new Monitor(new LuceneQueryParser("field"), new TermFilteredPresearcher());

        MonitorQuery[] mq = new MonitorQuery[]{
                new MonitorQuery("bogor","081110"),
                new MonitorQuery("bandung","0811129"),
                new MonitorQuery("solo","08112"),
                new MonitorQuery("bali","\"pulau dewata\"")
        };

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
                System.out.println("success");
            }
        }

    }

}
