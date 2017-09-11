// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.search.result.test;

import com.yahoo.search.Query;
import com.yahoo.search.result.ErrorHit;
import com.yahoo.search.result.ErrorMessage;
import com.yahoo.search.result.Hit;
import com.yahoo.search.result.HitGroup;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author bratseth
 */
public class HitGroupTestCase {

    @Test
    public void testStringStripping() {
        assertEquals("avabarne", Hit.stripCharacter('j', "javabjarne"));
        assertEquals("", Hit.stripCharacter('j', ""));
        assertEquals("", Hit.stripCharacter('j', "j"));
        assertEquals("frank", Hit.stripCharacter('j', "frank"));
        assertEquals("foo", Hit.stripCharacter('j', "fooj"));
        assertEquals("", Hit.stripCharacter('j', "jjjjj"));
    }
    
    @Test
    public void testErrors() {
        HitGroup hits = new HitGroup();

        Query query = new Query();
        query.errors().add(ErrorMessage.createIllegalQuery("test1"));
        query.errors().add(ErrorMessage.createTimeout("test2"));
        hits.setQuery(query);
        
        hits.addError(ErrorMessage.createForbidden("test3"));
        hits.addError(ErrorMessage.createUnspecifiedError("test4"));
        
        assertEquals(4, hits.getErrorHit().errors().size());
        assertEquals(0, query.errors().size());
        
        ErrorHit errors = hits.removeErrorHit();
        assertNotNull(errors);
        assertEquals(4, errors.errors().size());
        assertNull(hits.removeErrorHit());
    }

    @Test
    public void testRecursiveGet() {
        // Level 1
        HitGroup g1=new HitGroup();
        g1.add(new Hit("1"));

        // Level 2
        HitGroup g1_1=new HitGroup();
        g1_1.add(new Hit("1.1"));
        g1.add(g1_1);

        HitGroup g1_2=new HitGroup();
        g1_2.add(new Hit("1.2"));
        g1.add(g1_2);

        // Level 3
        HitGroup g1_1_1=new HitGroup();
        g1_1_1.add(new Hit("1.1.1"));
        g1_1.add(g1_1_1);

        HitGroup g1_1_2=new HitGroup();
        g1_1_2.add(new Hit("1.1.2"));
        g1_1.add(g1_1_2);

        HitGroup g1_2_1=new HitGroup();
        g1_2_1.add(new Hit("1.2.1"));
        g1_2.add(g1_2_1);

        HitGroup g1_2_2=new HitGroup();
        g1_2_2.add(new Hit("1.2.2"));
        g1_2.add(g1_2_2);

        // Level 4
        HitGroup g1_1_1_1=new HitGroup();
        g1_1_1_1.add(new Hit("1.1.1.1"));
        g1_1_1.add(g1_1_1_1);

        assertNotNull(g1.get("1"));
        assertNotNull(g1.get("1.1"));
        assertNotNull(g1.get("1.2"));
        assertNotNull(g1.get("1.1.1"));
        assertNotNull(g1.get("1.1.2"));
        assertNotNull(g1.get("1.2.1"));
        assertNotNull(g1.get("1.2.2"));
        assertNotNull(g1.get("1.1.1.1"));

        assertNotNull(g1.get("1",-1));
        assertNotNull(g1.get("1.1",-1));
        assertNotNull(g1.get("1.2",-1));
        assertNotNull(g1.get("1.1.1",-1));
        assertNotNull(g1.get("1.1.2",-1));
        assertNotNull(g1.get("1.2.1",-1));
        assertNotNull(g1.get("1.2.2",-1));
        assertNotNull(g1.get("1.1.1.1",-1));

        assertNotNull(g1.get("1",0));
        assertNull(g1.get("1.1",0));
        assertNull(g1.get("1.2",0));
        assertNull(g1.get("1.1.1",0));
        assertNull(g1.get("1.1.2",0));
        assertNull(g1.get("1.2.1",0));
        assertNull(g1.get("1.2.2",0));
        assertNull(g1.get("1.1.1.1",0));

        assertNotNull(g1.get("1",1));
        assertNotNull(g1.get("1.1",1));
        assertNotNull(g1.get("1.2",1));
        assertNull(g1.get("1.1.1",1));
        assertNull(g1.get("1.1.2",1));
        assertNull(g1.get("1.2.1",1));
        assertNull(g1.get("1.2.2",1));
        assertNull(g1.get("1.1.1.1",1));

        assertNotNull(g1.get("1",2));
        assertNotNull(g1.get("1.1",2));
        assertNotNull(g1.get("1.2",2));
        assertNotNull(g1.get("1.1.1",2));
        assertNotNull(g1.get("1.1.2",2));
        assertNotNull(g1.get("1.2.1",2));
        assertNotNull(g1.get("1.2.2",2));
        assertNull(g1.get("1.1.1.1",2));

        assertNotNull(g1.get("1.1.1.1",3));

        assertNull(g1.get("3",2));
    }

    @Test
    public void testThatHitGroupIsUnFillable() {
        HitGroup hg = new HitGroup("test");
        {
            Hit hit = new Hit("http://nalle.balle/1.html", 832);
            hit.setField("url", "http://nalle.balle/1.html");
            hit.setField("clickurl", "javascript:openWindow('http://www.foo');");
            hit.setField("attributes", Arrays.asList("typevideo"));
            hg.add(hit);
        }
        {
            Hit hit = new Hit("http://nalle.balle/2.html", 442);
            hit.setField("url", "http://nalle.balle/2.html");
            hit.setField("clickurl", "");
            hit.setField("attributes", Arrays.asList("typevideo"));
            hg.add(hit);
        }
        assertFalse(hg.isFillable());
        assertTrue(hg.isFilled("anyclass"));
        assertNull(hg.getFilled());
    }

    @Test
    public void testThatHitGroupIsFillable() {
        HitGroup hg = new HitGroup("test");
        {
            Hit hit = new Hit("http://nalle.balle/1.html", 832);
            hit.setField("url", "http://nalle.balle/1.html");
            hit.setField("clickurl", "javascript:openWindow('http://www.foo');");
            hit.setField("attributes", Arrays.asList("typevideo"));
            hit.setFillable();
            hg.add(hit);
        }
        {
            Hit hit = new Hit("http://nalle.balle/2.html", 442);
            hit.setField("url", "http://nalle.balle/2.html");
            hit.setField("clickurl", "");
            hit.setField("attributes", Arrays.asList("typevideo"));
            hit.setFillable();
            hg.add(hit);
        }
        assertTrue(hg.isFillable());
        assertFalse(hg.isFilled("anyclass"));
        assertTrue(hg.getFilled().isEmpty());
    }

    @Test
    public void testThatHitGroupIsFillableAfterFillableChangeunderTheHood() {
        HitGroup hg = new HitGroup("test");
        {
            Hit hit = new Hit("http://nalle.balle/1.html", 832);
            hit.setField("url", "http://nalle.balle/1.html");
            hit.setField("clickurl", "javascript:openWindow('http://www.foo');");
            hit.setField("attributes", Arrays.asList("typevideo"));
            hg.add(hit);
        }
        {
            Hit hit = new Hit("http://nalle.balle/2.html", 442);
            hit.setField("url", "http://nalle.balle/2.html");
            hit.setField("clickurl", "");
            hit.setField("attributes", Arrays.asList("typevideo"));
            hg.add(hit);
        }
        assertFalse(hg.isFillable());
        assertTrue(hg.isFilled("anyclass"));

        for (Hit h : hg.asList()) {
            h.setFillable();
        }

        HitGroup toplevel = new HitGroup("toplevel");
        toplevel.add(hg);

        assertTrue(toplevel.isFillable());
        assertNotNull(toplevel.getFilled());
        assertFalse(toplevel.isFilled("anyclass"));

        assertTrue(hg.isFillable());
        assertNotNull(hg.getFilled());
        assertFalse(hg.isFilled("anyclass"));
        assertTrue(hg.getFilled().isEmpty());
    }

}
