# zetcodetestjava

Please find the typical Multipartform file upload request template below

------------------------------------------------------------------------

/**
 * 
 *  
 *  POST /index HTTP/1.1
	Host: localhost
	User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36
	Content-Type: multipart/form-data; boundary=--eab528da-8c70-424d-8746-c18ee89f036c
	
	--eab528da-8c70-424d-8746-c18ee89f036c
	Content-Disposition: form-data; name="username"
	Content-Type: text/plain; charset=utf-8
	
	test_name
	--eab528da-8c70-424d-8746-c18ee89f036c
	Content-Disposition: form-data; name="password"
	Content-Type: text/plain; charset=utf-8
	
	test_psw
	--eab528da-8c70-424d-8746-c18ee89f036c
	Content-Disposition: form-data; name="imgFile"; filename="test.png"
	Content-Type: image/png
	Content-Transfer-Encoding: binary
	
	(data)
	--eab528da-8c70-424d-8746-c18ee89f036c--
 * 
 */ 
 
 The Flow of the Program is below
 
 