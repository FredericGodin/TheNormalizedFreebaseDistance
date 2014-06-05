import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Basic implementation of the Normalized Freebase Distance
 * Link to paper: http://2014.eswc-conferences.org/sites/default/files/eswc2014pd_submission_73.pdf
 * Link to poster: http://www.slideshare.net/fgodin/the-normalized-freebase-distance-nfd
 */

/**
 *
 * @author Fréderic Godin, frederic.godin@ugent.be
 *          Multimedia Lab, Ghent University - iMinds
 */
public class NormalizedFreebaseDistanceSimple {

    private static String singleWhereClause =  "?s ?p <http://rdf.freebase.com/ns/@queryTermToFillIn>" +
   ". FILTER (?p != <http://www.w3.org/2000/01/rdf-schema#label>) . FILTER (?p != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>)  . FILTER (isURI(?s))  . FILTER( !strStarts(str(?p),\"http://rdf.freebase.com/ns/common.\"))";

    private static double N= 50 * (int)Math.pow(10,6);

    // add here a SPARQL endpoint URI for Freebase!
    public static String freebaseURL = "";

    public static int getPageCount(String queryTerm) {
        queryTerm = ( queryTerm.charAt(0)=='/' ? queryTerm.substring(1) : queryTerm).replace("/", ".");
        String query = "SELECT COUNT(DISTINCT ?s) WHERE {" +singleWhereClause.replace("@queryTermToFillIn", queryTerm)+"}";
        JSONObject result = queryFreebase(query);
        try {
            return Integer.parseInt(((JSONObject)result.getJSONObject("results").getJSONArray("bindings").get(0)).getJSONObject(".1").getString("value"));
        } catch (JSONException ex) {
            Logger.getLogger(NormalizedFreebaseDistanceSimple.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public static String getIncomingLinks(String queryTerm) {
        queryTerm = ( queryTerm.charAt(0)=='/' ? queryTerm.substring(1) : queryTerm).replace("/", ".");
        String query = "SELECT ?s ?p ?o WHERE {"+singleWhereClause.replace("@queryTermToFillIn", queryTerm)+"}";
        JSONObject result = queryFreebase(query);
        return result.toString();
    }


    public static int getPageCount(String queryTerm1, String queryTerm2) {
        queryTerm1 = ( queryTerm1.charAt(0)=='/' ? queryTerm1.substring(1) : queryTerm1).replace("/", ".");
        queryTerm2 = ( queryTerm2.charAt(0)=='/' ? queryTerm2.substring(1) : queryTerm2).replace("/", ".");
        String query = "SELECT COUNT(DISTINCT ?s) WHERE {" +singleWhereClause.replace("@queryTermToFillIn", queryTerm1).replace("?p","?p1")+" . "+singleWhereClause.replace("@queryTermToFillIn", queryTerm2).replace("?p","?p2")+"}";
        JSONObject result = queryFreebase(query);
        try {
            return Integer.parseInt(((JSONObject)result.getJSONObject("results").getJSONArray("bindings").get(0)).getJSONObject(".1").getString("value"));
        } catch (JSONException ex) {
            Logger.getLogger(NormalizedFreebaseDistanceSimple.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }


    public static String getIncomingLinks(String queryTerm1, String queryTerm2) {
        queryTerm1 = ( queryTerm1.charAt(0)=='/' ? queryTerm1.substring(1) : queryTerm1).replace("/", ".");
        queryTerm2 = ( queryTerm2.charAt(0)=='/' ? queryTerm2.substring(1) : queryTerm2).replace("/", ".");
        String query = "SELECT ?s ?p1 ? WHERE {" +singleWhereClause.replace("@queryTermToFillIn", queryTerm1).replace("?p","?p1")+" . "+singleWhereClause.replace("@queryTermToFillIn", queryTerm2).replace("?p","?p2")+"}";
        JSONObject result = queryFreebase(query);
        return result.toString();
    }

    
    private static JSONObject queryFreebase(String query){
        try{
            
            String url = freebaseURL;
            URIBuilder builder = new URIBuilder(url);
            builder.addParameter("query",query);
            builder.addParameter("output", "json");
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(builder.build());

            // add request header
            request.addHeader("User-Agent", "Mozilla/5.0");
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result_buffer = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result_buffer.append(line);
            }
             JSONObject json = new JSONObject(result_buffer.toString());
             return json;
        }catch(IOException ex){
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        return null;
  }
  
    /**
     * @return The NFD between the provided terms.
     */
    public static double getNFD(String firstTerm, String secondTerm){
        Integer x = getPageCount(firstTerm);
        Integer y = getPageCount(secondTerm);
        Integer xy = getPageCount(firstTerm, secondTerm);
        return getNFD(x, y, xy, N);
    }

    /**
     * @param num_x Number of occurrences of query term x.
     * @param num_y Number of occurrences of query term y
     * @param num_xy Number of occurences of query term x AND y.
     * @param N Size of the KB e.g. number of entities
     * @return The Normalized Freebase NormalizedDistance
     */
    public static double getNFD(int num_x, int num_y, int num_xy, double N){

        if(num_x == 0 || num_y == 0 || num_xy == 0){
            return Double.POSITIVE_INFINITY;
        }

        double log_x = Math.log(num_x);
        double log_y = Math.log(num_y);
        double log_xy = Math.log(num_xy);

        return (Math.max(log_x, log_y) - log_xy)
                / (Math.log(N)  - Math.min(log_x, log_y));
    }

    public static void main(String [] args){

        if(NormalizedFreebaseDistanceSimple.freebaseURL.equals("")){
            System.out.println("Please set the URL of a Freebase SPARQL endpoint");
        }else{
            System.out.println("The Distance between Salmon and Trout is:" + NormalizedFreebaseDistanceSimple.getNFD("/m/09777", "/m/0cqpb"));
        }
    }


    
}
