package gov.va.contentlib.activities;

import gov.va.contentlib.ContentDBHelper;
import gov.va.contentlib.TopContentActivity;
import gov.va.contentlib.Util;
import gov.va.contentlib.content.Content;
import gov.va.contentlib.controllers.ContentViewController;
import gov.va.contentlib.controllers.ContentViewControllerBase;
import gov.va.contentlib.controllers.NavController;
import gov.va.contentlib.controllers.SUDSController;

import java.util.TreeMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.openmhealth.ohmage.campaigns.va.ptsd_explorer.PostExerciseSudsEvent;
import com.openmhealth.ohmage.campaigns.va.ptsd_explorer.PreExerciseSudsEvent;
import com.openmhealth.ohmage.campaigns.va.ptsd_explorer.ToolAbortedEvent;
import com.openmhealth.ohmage.core.EventLog;
/*
public class ManageNavigationController extends NavController {

	Content symptom;
	Content preselectedExercise;
	MenuItem favoritesItem; 
	int state;
	int lastRandomExercise;
	long lastRandomExerciseCategory;
	long lastCategoryIntro = -1;
	
	Integer sudsScore;
	Integer resudsScore;
	
	private static final int STATE_SYMPTOM_SELECTION = 1;
	private static final int STATE_SUDS = 2;
	private static final int STATE_EXERCISE = 3;
	private static final int STATE_RESUDS = 4;
	private static final int STATE_COMPARE = 5;
	
//	public static final int BUTTON_NEW_TOOL = 102;
	public static final int BUTTON_RETRY_WITH_NEW_TOOL = 110;
	public static final int BUTTON_DONE_EXERCISE = 103;
	public static final int BUTTON_NEXT = 104;
	public static final int BUTTON_DONE_RESUDS = 105;
	public static final int BUTTON_DONE_ALL = 106;

	public static final int RESULT_FAVORITE_SELECTION = 1000;

	public ManageNavigationController(Context ctx) {
		super(ctx);
	}
	
	@Override
	public void build() {
		super.build();
		state = STATE_SYMPTOM_SELECTION;
		lastRandomExercise=-1;
		lastRandomExerciseCategory=-1;
		lastCategoryIntro = -1;
	}
	
	public void symptomOrFavoriteSelected() {
		state = STATE_SUDS;
		sudsScore = null;
		Content c = db.getContentForName("sudsprompt");
		if (c != null) {
			ContentViewController cv = (ContentViewController)c.createContentView(getContext());
			cv.addButton("Skip").setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					distressLevelDone();
				}
			});
			cv.addButton("Next").setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					SUDSController cv = (SUDSController)getCurrentContentView();
					Integer distress = cv.getScore();
					if (distress == null) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder.setTitle("Rate Your Distress");
						builder.setMessage("Please either rate your distress using the meter or tap 'Skip'.");
						builder.setPositiveButton("Ok", null);
						builder.show();
						return;
					}
					recordSUDS();
					if ((sudsScore != null) && (sudsScore.intValue() >= 9)) {
						state = STATE_EXERCISE;
						pushViewForContent(db.getContentForName("crisis"));
						return;
					}
					distressLevelDone();
				}
			});
			pushView(cv,1,null);
		} else {
			distressLevelDone();
		}
	}

	public void exerciseDone() {
		state = STATE_RESUDS;
		resudsScore = null;
		Content c = db.getContentForName("sudsreprompt");
		if (c != null) {
			ContentViewController cv = (ContentViewController)c.createContentView(getContext());
			if (sudsScore == null) {
				cv.addButton("Try Another Tool",BUTTON_RETRY_WITH_NEW_TOOL);
				cv.addButton("Done",BUTTON_DONE_ALL);
			} else {
				cv.addButton("Next",BUTTON_DONE_RESUDS);
			}
			pushView(cv,1,null);
		} else {
			popToRoot();
		}
	}

	public void distressLevelDone() {
		state = STATE_EXERCISE;
		selectExerciseController(false);
	}

	public void recordReSUDS() {
		SUDSController cv = (SUDSController)getCurrentContentView();
		resudsScore = cv.getScore();

		TreeMap<String,String> map = new TreeMap<String, String>();
		map.put("suds",""+sudsScore);
		map.put("resuds",""+resudsScore);
		FlurryAgent.logEvent("RESUDS_READING",map);

		PostExerciseSudsEvent e = new PostExerciseSudsEvent();
		e.initialSudsScore = sudsScore;
		e.postExerciseSudsScore = resudsScore;
		EventLog.log(e);
	}

	public void compareSUDS() {
		if ((sudsScore != null) && (resudsScore != null)) {
			state = STATE_COMPARE;
			String contentName = "sudssame";
			if (sudsScore < resudsScore) {
				contentName = "sudsup";
			} else if (sudsScore > resudsScore) {
				contentName = "sudsdown";
			}
			Content c = db.getContentForName(contentName);
			ContentViewController cv = (ContentViewController)c.createContentView(getContext());
			cv.addButton("Try Another Tool",BUTTON_RETRY_WITH_NEW_TOOL);
			cv.addButton("Done",BUTTON_DONE_ALL);
			pushView(cv,1,null);
		} else {
			state = STATE_SYMPTOM_SELECTION;
			popToRoot();
		}
	}

	public void contentSelected(Content content) {
		// The content object is the symptom we need to manage
		symptom = content;
		preselectedExercise = null;
		lastRandomExercise=-1;
		lastRandomExerciseCategory=-1;
		lastCategoryIntro=-1;
		symptomOrFavoriteSelected();
	}
	
	@Override
	public void popView() {
		super.popView();
		if (stack.size() == 1) state = STATE_SYMPTOM_SELECTION;
	}

	@Override
	public void popToRoot() {
		super.popToRoot();
		state = STATE_SYMPTOM_SELECTION;
	}

	@Override
	public void buttonTapped(int id) {
		if (state == STATE_SYMPTOM_SELECTION) {
			super.buttonTapped(id);
			return;
		} else if (id == BUTTON_NEW_TOOL) {
			ToolAbortedEvent e = new ToolAbortedEvent();
			e.toolId = getCurrentContent().uniqueID;
			e.toolName = getCurrentContent().name;
			EventLog.log(e);
			selectExerciseController(true);
		} else if (id == BUTTON_RETRY_WITH_NEW_TOOL) {
			selectExerciseController(true);
		} else if (id == BUTTON_DONE_EXERCISE) {
			exerciseDone();
		} else if (id == BUTTON_DONE_RESUDS) {
			recordReSUDS();
			compareSUDS();
		} else if (id == BUTTON_DONE_ALL) {
			popToRoot();
		} else if (id == BUTTON_NEXT) {
			pushViewForContent(getCurrentContent().getNext());
		}
	}
	
	static final private int MAX_TRIES = 10;
	
	public void selectExerciseController(boolean flipNotPush) {
		Content exercise = null;
		ContentViewControllerBase cv = null;

		while (true) {
			int randPos = -1;
			exercise = null;
			
			if (preselectedExercise != null) {
				exercise = preselectedExercise;
			} else {
				Cursor c = db.sql().rawQuery("select content._id,content.weight,content.parent from content left join symptom_map on content._id=symptom_map.referrer where symptom_map.referree=?", new String[] {""+symptom.id});

				if (c.moveToFirst()) {
					boolean otherCategoryAvailable = false;
					float totalWeight = 0;

					while (true) {
						totalWeight += c.getFloat(1);
						if (c.getLong(2) != lastRandomExerciseCategory) {
							otherCategoryAvailable = true;
						}
						if (!c.moveToNext()) break;
					}

					int tryCount = 0;
					do {
						do {
							float chosen = (float)(totalWeight * Math.random());

							float iterWeight = 0;
							c.moveToFirst();
							while (true) {
								float thisWeight = c.getFloat(1);
								if (iterWeight+thisWeight >= chosen) break;
								iterWeight += thisWeight;
								if (!c.moveToNext()) break;
							}

							randPos = c.getPosition();
							tryCount++;
						} while ((randPos == lastRandomExercise) && (tryCount < MAX_TRIES));

						exercise = db.getContentForID(c.getLong(0));
					} while ((exercise.getParentID() == lastRandomExerciseCategory) && otherCategoryAvailable && (tryCount < MAX_TRIES));

					c.close();
				}
			}

			if (exercise == null) break;
			
			cv = exercise.createContentView(getContext());
			cv.setSelectedContent(exercise);

			ContentViewControllerBase proxy = cv.checkProxy();
			if (proxy != null) cv = proxy;
			
			String prereq = cv.checkPrerequisites();
			if (prereq != null) {
				if (preselectedExercise != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
					builder.setTitle("Can't use this tool");
					builder.setMessage(prereq);
					builder.setPositiveButton("Ok", null);
					builder.show();
					state = STATE_SYMPTOM_SELECTION;
					popToRoot();
					return;
				}
				
				continue;
			}
			
			lastRandomExercise = randPos;
			lastRandomExerciseCategory = exercise.getParentID();
			break;
		}
	
		if (exercise == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle("Problem finding exercise");
			builder.setMessage("Sorry, I couldn't find an exercise right now.");
			builder.setPositiveButton("Ok", null);
			builder.show();
			state = STATE_SYMPTOM_SELECTION;
			popToRoot();
			return;
		}

		if (lastCategoryIntro != exercise.getParentID()) {
			Content parent = exercise.getParent();
			if (parent != null) {
				String parentUI = parent.getUIDescriptor();
				if (parentUI != null) {
					cv = parent.createContentView(getContext());
					cv.setSelectedContent(exercise);
					lastCategoryIntro = exercise.getParentID();
				}
			}
		}
    	
    	if (flipNotPush) {
    		flipReplaceView(cv,2);
    	} else {
    		pushView(cv);
    	}
	}
		
}
*/

