
//Dummy call we can use curl to test out...
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});


//BackgroundActivity.java is responsible for updating the points
//What we want to do is make sure the points only increment upward by a single point here
Parse.Cloud.beforeSave("Entry", function(request, response) {
  
    Parse.Cloud.useMasterKey(); //allow ACL updates
    var publicACL = new Parse.ACL();
    publicACL.setPublicWriteAccess(true);
     publicACL.setPublicReadAccess(true);
    request.object.setACL(publicACL);
    
	var originalPoints = -1;
	var requestPoints = request.object.get("points");
	var userName = request.object.get("username");
  	console.log("request params: "+ JSON.stringify(request) );
  	
	// Query for all users to find who we are 
	//(since we don't pass that in the request nor have to have a valid user account)
	var Entry = Parse.Object.extend("Entry");
	var query = new Parse.Query(Entry);
	query.equalTo("username", userName);
	query.find( {
		success: function(users) {
	    	//console.log("Query found a match!");   
	    	//console.log("Number of users="+users.length);
	    	if(users.length>=0)
	    	{
				originalPoints = users[0].get("points");
        		
        		desiredPnts = originalPoints+1;
        		if(requestPoints > desiredPnts)
        			desiredPnts = requestPoints;
        			//NOTE: for some reason this request isn't going through. Looks like the updatedAt JSON code is always older. Why?
        		request.object.set("points",desiredPnts); //modify the request points
        		console.log("Adjusting "+userName+" points from "+ originalPoints+" to "+desiredPnts);
        		delete request["ACL"]; //possible to remove ACL here?
        		console.log("request params NOW: "+ JSON.stringify(request) );
         		//see: https://parse.com/docs/cloud_code_guide#functions
         			
         		if ((originalPoints+1)==requestPoints) {
      				console.log("Points look good!");
  				}
  				else
  				{
	    			console.log("Fixing points correctly! (from " + originalPoints + " to " + requestPoints + " was wrong)");
  				}
  				
  				users[0].set("points", desiredPnts); //save our score manually
  				
  				//users[0].save(); //commit the save manually created feedback loop.. hmmm?
  				
		        response.success(); //don't continue the save				
         	}
  
      	},
      	error: function(error) {
      		console.error("Error finding username="+userName+" with " + error.code + ": " + error.message);
      		response.success(); //just continue, let the code do it's thing..
      	}    	
  	});
  
});


Parse.Cloud.afterSave(Parse.User, function(request) {
  //ensure ACL on all new users to protect PII
  var user = request.user;
  if (!user.existed()) {
    var userACL = new Parse.ACL(user);
    acl.setPublicReadAccess(true)
	acl.setPublicWriteAccess(true)
    user.setACL(userACL);
    user.save();
    console.log("Saved ACL for new user");   
  } else {
    //try to fix the ACL
  	var userACL = user.getACL();
  	userACL.setPublicWriteAccess(true);
  	userACL.setPublicReadAccess(true);
  	user.save();
  	 console.log("User already has ACL");   
  }
});


Parse.Cloud.define("updateAll", function(request, response) {
  console.log("About to update all users to have full read+write access!!");
   
   Parse.Cloud.useMasterKey(); //allow ACL updates
   
    var publicACL = new Parse.ACL();
    publicACL.setPublicWriteAccess(true);
     publicACL.setPublicReadAccess(true);
    request.object.setACL(publicACL);
    
  var Entry = Parse.Object.extend("Entry");
  var routeQuery = new Parse.Query(Entry);
  routeQuery.each(
        function(user){
        	var userACL = user.getACL();
  			userACL.setPublicWriteAccess(true);
  			userACL.setPublicReadAccess(true);
  			user.save();
            console.log("Set ACL for user " + user.get("username"));
        }, 
        {
        success: function() {
            // results is an array of Parse.Object.
            console.log('@Query');
            response.success("Updated Records!!");
              },
        error: function(error) {
            // error is an instance of Parse.Error.
            console.log('@error');
            response.error("Failed to save user acl. Error=" + error.message);
        }
    });
});
  
