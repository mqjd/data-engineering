"use strict";(self.webpackChunkdata_docs=self.webpackChunkdata_docs||[]).push([[775],{857:(e,s,r)=>{r.r(s),r.d(s,{assets:()=>c,contentTitle:()=>t,default:()=>h,frontMatter:()=>i,metadata:()=>l,toc:()=>d});var n=r(4848),a=r(8453);const i={sidebar_position:3},t="Kafka",l={id:"cluster/kafka",title:"Kafka",description:"WebUI",source:"@site/docs/cluster/kafka.md",sourceDirName:"cluster",slug:"/cluster/kafka",permalink:"/data-engineering/docs/cluster/kafka",draft:!1,unlisted:!1,editUrl:"https://github.com/mqjd/data-engineering/tree/main/data-docs/docs/cluster/kafka.md",tags:[],version:"current",sidebarPosition:3,frontMatter:{sidebar_position:3},sidebar:"clusterSidebar",previous:{title:"Yarn",permalink:"/data-engineering/docs/cluster/hadoop/yarn"},next:{title:"ClickHouse",permalink:"/data-engineering/docs/cluster/clickhouse"}},c={},d=[{value:"WebUI",id:"webui",level:2},{value:"\u547d\u4ee4",id:"\u547d\u4ee4",level:2},{value:"Topic",id:"topic",level:3},{value:"create",id:"create",level:4},{value:"alter",id:"alter",level:4},{value:"describe",id:"describe",level:4},{value:"delete",id:"delete",level:4},{value:"list",id:"list",level:4},{value:"\u6d88\u606f",id:"\u6d88\u606f",level:3},{value:"\u57fa\u7840",id:"\u57fa\u7840",level:4},{value:"\u751f\u4ea7",id:"\u751f\u4ea7",level:5},{value:"\u6d88\u8d39",id:"\u6d88\u8d39",level:5},{value:"\u6279\u91cf",id:"\u6279\u91cf",level:4},{value:"\u751f\u4ea7",id:"\u751f\u4ea7-1",level:5},{value:"\u6d88\u8d39",id:"\u6d88\u8d39-1",level:5},{value:"\u538b\u6d4b",id:"\u538b\u6d4b",level:4},{value:"\u751f\u4ea7",id:"\u751f\u4ea7-2",level:5},{value:"\u6d88\u8d39",id:"\u6d88\u8d39-2",level:5},{value:"\u96c6\u7fa4\u64cd\u4f5c",id:"\u96c6\u7fa4\u64cd\u4f5c",level:2},{value:"\u542f\u52a8",id:"\u542f\u52a8",level:3},{value:"\u505c\u6b62",id:"\u505c\u6b62",level:3}];function o(e){const s={code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",h5:"h5",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,a.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(s.h1,{id:"kafka",children:"Kafka"}),"\n",(0,n.jsx)(s.h2,{id:"webui",children:"WebUI"}),"\n",(0,n.jsx)(s.p,{children:"\u6682\u65e0"}),"\n",(0,n.jsx)(s.h2,{id:"\u547d\u4ee4",children:"\u547d\u4ee4"}),"\n",(0,n.jsx)(s.h3,{id:"topic",children:"Topic"}),"\n",(0,n.jsx)(s.h4,{id:"create",children:"create"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-topics.sh --bootstrap-server hd1:9092 --create --topic hd-test-topic --partitions 2\n"})}),"\n",(0,n.jsx)(s.h4,{id:"alter",children:"alter"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-topics.sh --bootstrap-server hd1:9092 --alter --topic hd-test-topic --partitions 3\n"})}),"\n",(0,n.jsx)(s.h4,{id:"describe",children:"describe"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-topics.sh --bootstrap-server hd1:9092 --describe --topic hd-test-topic\n"})}),"\n",(0,n.jsx)(s.h4,{id:"delete",children:"delete"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-topics.sh --bootstrap-server hd1:9092 --delete --topic hd-test-topic\n"})}),"\n",(0,n.jsx)(s.h4,{id:"list",children:"list"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-topics.sh --bootstrap-server hd1:9092 --list\n"})}),"\n",(0,n.jsx)(s.h3,{id:"\u6d88\u606f",children:"\u6d88\u606f"}),"\n",(0,n.jsx)(s.h4,{id:"\u57fa\u7840",children:"\u57fa\u7840"}),"\n",(0,n.jsx)(s.h5,{id:"\u751f\u4ea7",children:"\u751f\u4ea7"}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u751f\u4ea7\u65e0key\u6d88\u606f"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-producer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --producer.config ${KAFKA_CONF_DIR}/producer.properties\n"})}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u751f\u4ea7\u6709key\u6d88\u606f"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-producer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --producer.config ${KAFKA_CONF_DIR}/producer.properties --property parse.key=true\n"})}),"\n",(0,n.jsx)(s.h5,{id:"\u6d88\u8d39",children:"\u6d88\u8d39"}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u4ece\u5f00\u59cb\u4f4d\u7f6e\u6d88\u8d39"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --from-beginning\n"})}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u663e\u793akey"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --from-beginning --property parse.key=true\n"})}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u6307\u5b9a\u5206\u533a\u548c\u504f\u79fb"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --partition 0 --offset 100 --from-beginning --property parse.key=true\n"})}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u8bbe\u7f6e\u6d88\u8d39\u7ec4"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --group hd-test-group\n"})}),"\n",(0,n.jsxs)(s.ul,{children:["\n",(0,n.jsx)(s.li,{children:"\u4f7f\u7528consumer\u914d\u7f6e\u6587\u4ef6"}),"\n"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --consumer.config ${KAFKA_CONF_DIR}/consumer.properties\n"})}),"\n",(0,n.jsx)(s.h4,{id:"\u6279\u91cf",children:"\u6279\u91cf"}),"\n",(0,n.jsx)(s.h5,{id:"\u751f\u4ea7-1",children:"\u751f\u4ea7"}),"\n",(0,n.jsxs)(s.p,{children:[(0,n.jsx)(s.strong,{children:"--max-messages"}),"\uff1a\u603b\u6761\u6570\uff0c",(0,n.jsx)(s.strong,{children:"--throughput"}),"\uff1a\u541e\u5410(\u6761/\u79d2)"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-verifiable-producer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --max-messages 10 --throughput 1\n"})}),"\n",(0,n.jsx)(s.h5,{id:"\u6d88\u8d39-1",children:"\u6d88\u8d39"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-verifiable-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --group-id hd-test-group --max-messages 10\n"})}),"\n",(0,n.jsx)(s.h4,{id:"\u538b\u6d4b",children:"\u538b\u6d4b"}),"\n",(0,n.jsx)(s.h5,{id:"\u751f\u4ea7-2",children:"\u751f\u4ea7"}),"\n",(0,n.jsxs)(s.p,{children:[(0,n.jsx)(s.strong,{children:"--max-messages"}),"\uff1a\u603b\u6761\u6570\uff0c",(0,n.jsx)(s.strong,{children:"--throughput"}),"\uff1a\u541e\u5410(\u6761/\u79d2)"]}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:'for ((i=1; i<=100; i++))\ndo\n    echo "\u8fd9\u662f\u7b2c$i\u884c\u6570\u636e" >> /opt/bigdata/kafka-payload-file.txt\ndone\n\nkafka-producer-perf-test.sh --topic hd-test-topic --num-records 100 --throughput 100 --producer-props bootstrap.servers=hd1:9092 --payload-file /opt/bigdata/kafka-payload-file.txt\n'})}),"\n",(0,n.jsx)(s.h5,{id:"\u6d88\u8d39-2",children:"\u6d88\u8d39"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-consumer-perf-test.sh --bootstrap-server hd1:9092 --topic hd-test-topic --group hd-test-group --messages 100\n"})}),"\n",(0,n.jsx)(s.h2,{id:"\u96c6\u7fa4\u64cd\u4f5c",children:"\u96c6\u7fa4\u64cd\u4f5c"}),"\n",(0,n.jsx)(s.h3,{id:"\u542f\u52a8",children:"\u542f\u52a8"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-server-start.sh -daemon $KAFKA_CONF_DIR/server.properties\n"})}),"\n",(0,n.jsx)(s.h3,{id:"\u505c\u6b62",children:"\u505c\u6b62"}),"\n",(0,n.jsx)(s.pre,{children:(0,n.jsx)(s.code,{className:"language-bash",children:"kafka-server-stop.sh\n"})})]})}function h(e={}){const{wrapper:s}={...(0,a.R)(),...e.components};return s?(0,n.jsx)(s,{...e,children:(0,n.jsx)(o,{...e})}):o(e)}},8453:(e,s,r)=>{r.d(s,{R:()=>t,x:()=>l});var n=r(6540);const a={},i=n.createContext(a);function t(e){const s=n.useContext(i);return n.useMemo((function(){return"function"==typeof e?e(s):{...s,...e}}),[s,e])}function l(e){let s;return s=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:t(e.components),n.createElement(i.Provider,{value:s},e.children)}}}]);