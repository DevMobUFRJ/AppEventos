// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFavoriteNotifications = functions.firestore.document("/semanas/{semana}/atividades/{atividade}").onUpdate(event =>{
    const splitedPath = event.after.ref.path.split("/");
    const data = event.after.data();
    if(data.favs != event.before.data().favs) return 0;

    const notificationContent = {
        data: {
            body: data.nome + " sofreu uma alteração, venha ver!",
            id: splitedPath[3],
            nome: data.nome,
            grupo: data.grupo,
            tipo: data.tipo,
            inicio: data.inicio._seconds.toString(),
            fim: data.fim._seconds.toString(),
            apresentador: data.apresentador,
            local: data.local,
            weekId: splitedPath[1],
//            favs: 0
        }
    }

//    if(data.favs != undefined) notificationContent.data.favs = data.favs;

    const weekDetails = admin.firestore().collection("semanas").doc(splitedPath[1]).get().then(semanaModificada => {
        notificationContent.data.title = semanaModificada.data().nome;
    });

    const usersRef = admin.firestore().collection("users");
    const listUsers =  usersRef.get();

    return Promise.all([listUsers, weekDetails]).then(result =>{
        const users = result[0]
        users.forEach(user => {
            usersRef.doc(user.data().uid).collection("favorites").where("id", "=", splitedPath[3]).get().then(favoritouEvento => {
                if(!favoritouEvento.empty) admin.messaging().sendToDevice(user.data().token, notificationContent);
            });
        });
    });
});

exports.onCreateFavorite = functions.firestore.document("/users/{user}/favorites/{favorito}").onCreate(event =>{
    const doc = event.data();
    const weekRef = admin.firestore().collection("semanas").doc(doc.weekId).collection("atividades").doc(doc.id);
    
    return weekRef.get().then(activity => {
        var newFavs = 1;
        const oldFavs = activity.data().favs;

        if(oldFavs != undefined) newFavs += oldFavs;
        weekRef.set({favs: newFavs}, { merge: true });
    });
});

exports.onDeleteFavorite = functions.firestore.document("/users/{user}/favorites/{favorito}").onDelete(event =>{
    const doc = event.data();
    const weekRef = admin.firestore().collection("semanas").doc(doc.weekId).collection("atividades").doc(doc.id);
    
    return weekRef.get().then(activity => {
        var newFavs = 0;
        const oldFavs = activity.data().favs;

        if(oldFavs != undefined && oldFavs >= 1) newFavs = oldFavs-1;
        weekRef.set({favs: newFavs}, { merge: true });
    });
});