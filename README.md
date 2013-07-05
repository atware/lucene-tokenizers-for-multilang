#lucene-tokenizers-for-multilang
多言語検索用のLucene/Solr用のプラグイン(TokenizerおよびFilter)です
##本プロジェクトについて
本プロジェクトは複雑な仕様の多言語検索の実現をサポートする事を目標としています  
Solrのスキーマの設定ファイルの例をsolr-schemaディレクトリ配下に配備しています  

##プラグインの利用方法
###jp.co.atware.solr.analizers.cjk.CJKTokenizerの設定
CJKTokenizerは各言語文字のトークン化の単位を設定ファイルにて柔軟に変更可能なTokenizerクラスです  
デフォルトの設定では各種言語文字に対して、下記の形式でトークン化を行います  
* アルファベット => word単位
* その他の言語の文字 => unigram + bigram  

各言語文字に対するトークン化の形式を変更する場合は、以下の手順で設定を行います  

1. schema.xmlのCJKTokenizerFactoryの属性に以下のものを追記  
custom="TRUE"  
action="action-filename"  
cmap="cmap-filename"  
trans="trans-filename"  
2. schema.xmlで記載した定義した名前のファイルをconfディレクトリ下に配置する
3. 後述の記載方法で上記ファイルの内容を記載する

####cmapファイルの記載方法
トークン化形式を指定する単位（以降クラスと記載）の定義を行います  
クラス番号は1～連続の整数値で指定してください.（クラス=0は予約クラスであるため使用できません）  
classの範囲は文字コード（UTF-16）で指定します.

####transファイルの記載方法
縦を現在の状態、横を入力文字のクラスとする状態遷移図を示します  
状態0は開始、-1は終了状態を示します  
開始から終了状態に遷移するまでに、読み込まれた文字列をトークン候補とします  
このトークン候補を実際にトークン化するか否かはactionファイルにて定義します  

####actionファイルの記載方法
各classのトークン化の種別を指定します  
* 0 => トークン化しない
* 1 => word
* 2 => unigram

トークン化するか否かは0 or 1,2で決定します.  
2を指定したトークンはCJKBigramFilterと組み合わせた時にunigram+bigramに変換されます.  
設定例としてexample-settings配下に設定ファイルを配備しています(設定例では下記のようにトークン化されます)
* 漢字・平仮名・片仮名・ハングル => unigram + bigram
* その他の言語の文字 => word単位

###jp.co.atware.solr.analizers.cjk.MultistageMappingCharFilterの設定
MultistageMappingCharFilterは複雑な正規化処理への対応を容易にするためのFilterクラスです  
MultistageMappingCharFilterはSolrのMappingCharFilterをベースとしており、設定方法はMappingCharFilterとほぼ同様です  
ベースと異なり、MultistageMappingCharFilterによる正規化処理は、複数ステージに分けて段階的に正規化を行うことが可能としています  
これは正規化処理による正規化後の文字が、別の正規化処理の正規化前の文字となるケースが多数存在する場合を想定しています  

####mappingファイルの定義方法
MultistageMappingCharFilterFactoryのmapping属性にマッピングファイルを定義します  
ステージの分割はmapping属性の';'セミコロンで行い、左のステージから順に正規化が実行されます  
同一ステージに正規化前の文字が複数定義されている場合は以下のように処理されます  
* 同一ステージで"A" => "B"と"A" => "C"が定義されている場合：Solr起動時にエラーとなる
* 同一ステージで"A" => "B"と"AD" => "C"が定義されている場合："AD" => "C"の正規化が優先される
