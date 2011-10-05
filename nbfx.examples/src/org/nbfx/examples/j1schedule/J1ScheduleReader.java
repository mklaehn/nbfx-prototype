/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.nbfx.examples.j1schedule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import net.sf.csv4j.*;
import org.openide.util.Exceptions;

/**
 *
 * @author Administrator
 */
public final class J1ScheduleReader {
    
    public static J1Schedule read() {
            final J1Schedule schedule = new J1Schedule();
        try {
            URL url = J1ScheduleReader.class.getResource("Oracle_San_Francisco_2011.csv");
            CSVStreamProcessor fileProcessor = new CSVStreamProcessor();
            fileProcessor.processStream(new InputStreamReader(url.openStream()), new CSVLineProcessor() {

                @Override
                public void processHeaderLine(int i, List<String> list) {
                }

                @Override
                public void processDataLine(int i, List<String> list) {
                    schedule.add(new J1Session(list.get(0),new Date(), new Date(), list.get(1), list.get(6)));
                }

                @Override
                public boolean continueProcessing() {
                    return true;
                }
            });
        } catch (ProcessingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return schedule;
    }
    
            
    public static class J1Schedule implements Iterable<J1Session>{
        private List<J1Session> sessions = new ArrayList<J1Session>();
        
        private void add(J1Session j1Session) {
            sessions.add(j1Session);
        }

        @Override
        public Iterator<J1Session> iterator() {
            return sessions.iterator();
        }
        
        public Collection<? extends J1Session> getSessions(){
            return sessions;
        }
    }        
    
    public static class J1Session {
        private final String id;
        private final Date start;
        private final Date end;
        private final String title;
        private final String location;

        public J1Session(String id, Date start, Date end, String title, String location) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.title = title;
            this.location = location;
        }

        public Date getEnd() {
            return end;
        }

        public String getLocation() {
            return location;
        }

        public Date getStart() {
            return start;
        }

        public String getTitle() {
            return title;
        }
        
        public String getId() {
            return id;
        }
        
        
    }
}
