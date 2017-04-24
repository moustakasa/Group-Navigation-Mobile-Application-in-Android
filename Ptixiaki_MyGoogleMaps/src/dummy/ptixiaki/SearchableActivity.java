package dummy.ptixiaki;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;


public class SearchableActivity extends ListActivity {
	
	/**
     * SearchableActivity called from MainActivity.
     * 
     * That is when user selects to create a new group of contacts, in the Action Bar/Options Menu.
     * When a user executes a search from the search dialog or widget, the system starts the searchable activity and sends it a ACTION_SEARCH intent.
     * This intent carries the search query in the QUERY string extra.
     * You must check for this intent when the activity starts and extract the string.
     */
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		System.out.println("SearchableActivity : enabled (from MainActivity)");
		
	    super.onCreate(savedInstanceState);
	    //TO_DO: setContentView(R.layout.activity_searchable);

	    
	    // Get the intent from search UI, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	//TO_DO: this.startActivityForResult(intent, -1);
	    	String query = intent.getStringExtra(SearchManager.QUERY);
		    doMySearch(query);
	    }
	}
	
	
	/**
	 *  public boolean doMySearch(String query)
	 * 
	 *  This is where the actual search for a matching contact, is done.
	 *  Access my contacts list, get me the ones i selected, and return them to me in a ListView.
	 * @param query
	 * @return
	 */
	
	public boolean doMySearch(String query){
		System.out.println("method doMySearch(String query) : enabled");
		
		return true;
	}
}
