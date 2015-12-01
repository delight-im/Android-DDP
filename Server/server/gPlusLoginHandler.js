
/*
* author: cprakashagr
* date: 30 Nov 2015
* fileName: gPlusLoginHandler.js
* comments: This file is required to override the LoginHandler method at server for Google Plus Login from Android DDP lib.
*           It makes sure that it will be called only at googleLoginPlugin == true;
*           Make sure that you have the clientId and the clientSecret from the google developer console.
*           The same clientId must be used in the Android side for the proper validation.
*/

Accounts.registerLoginHandler(function (req) {

    //  Your clientId and the clientSecret Initializations here.
    var googleApiKey = {

        clientId: "",
        clientSecret: ""
    };

    if (!req.googleLoginPlugin) {

        //console.log('Default registerLoginHandler will be called.');
        return;
    }

    var user = Meteor.users.findOne({

            // "services.google.email": req.email,
            "services.google.id": req.email
        }),
        userId = null;

    if (!user) {

        var res = Meteor.http.get("https://www.googleapis.com/oauth2/v3/tokeninfo", {
            headers: {
                "User-Agent": "Meteor/1.0"
            },

            params: {
                id_token: req.idToken
            }
        });

        if (res.error) throw res.error;
        else {
            if (req.userId == res.data.sub && res.data.aud == googleApiKey.clientId) {
                var googleResponse = _.pick(res.data, "email", "email_verified", "family_name", "gender", "given_name", "locale", "name", "picture", "profile", "sub");

                googleResponse["accessToken"] = req.oAuthToken;
                googleResponse["id"] = req.sub;

                if (typeof(googleResponse["email"]) == "undefined") {
                    googleResponse["email"] = req.email;
                }

                var insertObject = {
                    createdAt: new Date(),
                    // profile: googleResponse,
                    services: {
                        google: googleResponse
                    }
                };

                if (req.profile && (req.profile instanceof Array)) { // fill profile according to req.profile
                    if (0 < req.profile.length) {
                        insertObject.profile = {};

                        for (var A = 0; A < req.profile.length; A++) {
                            if (_.has(googleResponse, req.profile[A])) {
                                insertObject.profile[req.profile[A]] = googleResponse[req.profile[A]];
                            }
                        }
                    }
                }

                userId = Meteor.users.insert(insertObject);
            } else throw new Meteor.Error(422, "AccessToken MISMATCH in googleLoginPlugin");
        }
    } else userId = user._id;

    var stampedToken = Accounts._generateStampedLoginToken();
    var stampedTokenHash = Accounts._hashStampedToken(stampedToken);

    Meteor.users.update({
        _id: userId
    }, {
        $push: {
            "services.resume.loginTokens": stampedTokenHash
        }
    });

    console.log(userId);

    return {
        token: stampedToken.token,
        userId: userId
    };
});