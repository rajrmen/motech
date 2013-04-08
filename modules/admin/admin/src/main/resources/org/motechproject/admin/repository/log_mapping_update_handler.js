function(doc, req){
     if(!doc){
        if(req.id){
            return [{
                _id: req.id,
                logName: req.query.logName,
                logLevel: req.query.logLevel
            }, {}];
        }else{
            return [null, {}];
        }
    }else{
        doc.logName = req.query.logName;
        doc.logLevel = req.query.logLevel;

        return [doc, {}];
    }
}