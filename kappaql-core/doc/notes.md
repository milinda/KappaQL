# KappaQL Query Layer Design Notes

* First problem is what is the serialization format of the events comes in to Kafka from outside world. For the 
  prototype we can use flat JSON objects.
* Then how we are going to define the stream:
    > Given that we choose JSON as the serialization format above, we can just use a mapping of fields to their types 
    > as the stream definition. Then the problem is how we annotate the ID/Primary Key of this stream in the definition.
    > And also which field contains the timestamp. In the first version its mandatory to have a timestamp field.
    > We can use something like follows.
    
    > ```clojure
    > (defstream stream
    >     (fields [:name :string :address :string :age :integer :timestamp :long])
    >     (pk :id)
    >     (ts :timestamp))
    > ```
    
