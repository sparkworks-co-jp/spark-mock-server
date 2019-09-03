### 起動
run.batから起動

>デフォルトでは、port:8888利用している。変えたい際、run@8889.batご参照ください

### 使用例

REQUEST: /customers POST
RESPONSE FILE: response-json/customers/POST.json

REQUEST: /customers GET
RESPONSE FILE: response-json/customers/GET.json

REQUEST: /customers/1 GET
RESPONSE FILE: response-json/customers/1/GET.json

REQUEST: /customers?name=cc GET
RESPONSE FILE: response-json/customers/name=cc/GET.json

REQUEST: /customers?name=cc&birthday=19800101 GET
RESPONSE FILE: response-json/customers/name=cc&birthday=19800101/GET.json
