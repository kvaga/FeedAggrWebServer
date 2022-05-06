/**
 * 
 */
//const influxdbUrl = "http://localhost:8080/FeedAggrWebServer/Test?org=e865876876504&bucket=FirstBucket?org=e84876876876d702504&bucket=FirstBucket";
const influxdbUrl="https://europe-west1-1.gcp.cloud2.influxdata.com/api/v2/write?org=6876876876702504&bucket=FirstBucket&precision=ns";
let client = new XMLHttpRequest();

function influxdbSend2(metric/*, value*/){
        	
            // Important: you must 
        	//   1. add 'internet' privilege in the config.xml
        	//   2. set policy  access for your full url and set 'allow subdomain' to 'true'
            var html = '';
            /* Assign request type and server path */
            client.open("POST", influxdbUrl, true);
			client.setRequestHeader('Authorization', 'Token hbjbjbjhbjbjh==')
            client.setRequestHeader('Content-Type', 'text/plain; charset=utf-8');
            client.setRequestHeader('Accept','application/json');
            	client.onerror = function(e) { // происходит, только когда запрос совсем не получилось выполнить
            	  console.error('Connection error for url ['+influxdbUrl+']. e: ' + e + ', e.target.status: ' + e.target.status);
            	};

            	client.onprogress = function(event) { // запускается периодически
            	  // event.loaded - количество загруженных байт
            	  // event.lengthComputable = равно true, если сервер присылает заголовок Content-Length
            	  // event.total - количество байт всего (только если lengthComputable равно true)
            	  console.log('Loaded '+event.loaded+' from '+event.total);
            	};
            
            client.onreadystatechange = function() {//Вызывает функцию при смене состояния.
                if(client.readyState == XMLHttpRequest.DONE) {
                	if(client.status == 200 || client.status == 204){
                		// Запрос завершён. Здесь можно обрабатывать результат.
                        console.log('OK: ' + client.responseText);
                	}else{
                		console.log('XMLHttpRequest.DONE: ' + client.status);
                	}
                }else if(client.readyState==XMLHttpRequest.HEADERS_RECEIVED){
                	console.log('HEADERS_RECEIVED: ' + client.getAllResponseHeaders());
                }else if(client.readyState==XMLHttpRequest.OPENED){
                	console.log('CONNECTION OPENED ['+influxdbUrl+']');
                }else if(client.readyState==XMLHttpRequest.LOADING){
                	console.log('LOADING...');
                	console.log('Loaded '+event.loaded+' from '+event.total);
                }else{
                	console.error("["+XMLHttpRequest.DONE+"] client.state: " + client.readyState + ", client.status: " + client.status);
                }
            }
           
            metric = metric + ' ' + Math.floor( new Date() / 1000 )+'000000000';
            console.log('sent metric: ' + metric);
            client.send(metric);
 }
        
