package com.syncleus.spangraph;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.util.*;
import org.infinispan.commons.util.InfinispanCollections;
import org.infinispan.commons.util.WeakValueHashMap;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blueprints Graph interface with adjacency implemented by some Map implementation.
 * Iterables in Elements, Vertices, and Edges are implemented by guava Iterator/Iterable lazy wrappers, avoiding collection allocation
 */
abstract public class MapGraph<X extends Serializable> implements Graph {

    private static Map<String,MapGraph> global = new WeakValueHashMap<>();

    /** name of the graph combined with the peerID */
    public final String id;

    /** name of the graph as shared on the network */
    public final String globalID;

    protected Map<X, Vertex> vertices;
    protected Map<X, Edge> edges;

    //protected Map<String, TinkerIndex> indices = new HashMap<String, TinkerIndex>();
    //protected TinkerKeyIndex<InfiniVertex> vertexKeyIndex = new TinkerKeyIndex<InfiniVertex>(InfiniVertex.class, this);
    //protected TinkerKeyIndex<InfiniEdge> edgeKeyIndex = new TinkerKeyIndex<InfiniEdge>(InfiniEdge.class, this);

    private static final Features FEATURES = new Features();


    static {
        FEATURES.supportsDuplicateEdges = true;
        FEATURES.supportsSelfLoops = true;
        FEATURES.supportsSerializableObjectProperty = true;
        FEATURES.supportsBooleanProperty = true;
        FEATURES.supportsDoubleProperty = true;
        FEATURES.supportsFloatProperty = true;
        FEATURES.supportsIntegerProperty = true;
        FEATURES.supportsPrimitiveArrayProperty = true;
        FEATURES.supportsUniformListProperty = true;
        FEATURES.supportsMixedListProperty = true;
        FEATURES.supportsLongProperty = true;
        FEATURES.supportsMapProperty = true;
        FEATURES.supportsStringProperty = true;

        FEATURES.ignoresSuppliedIds = false;
        FEATURES.isPersistent = false;
        FEATURES.isWrapper = false;

        FEATURES.supportsIndices = false;
        FEATURES.supportsKeyIndices = false;
        FEATURES.supportsVertexKeyIndex = false;
        FEATURES.supportsEdgeKeyIndex = false;
        FEATURES.supportsVertexIndex = false;
        FEATURES.supportsEdgeIndex = false;
        FEATURES.supportsTransactions = false;
        FEATURES.supportsVertexIteration = true;
        FEATURES.supportsEdgeIteration = true;
        FEATURES.supportsEdgeRetrieval = true;
        FEATURES.supportsVertexProperties = true;
        FEATURES.supportsEdgeProperties = true;
        FEATURES.supportsThreadedTransactions = false;
        FEATURES.supportsThreadIsolatedTransactions = false;

    }

    public int vertexCount() {
        return vertexCollection().size();
    }
    public int edgeCount() {
        return edgeCollection().size();
    }



    protected MapGraph() {
        super();
        this.id = this.globalID = null;
    }

    protected MapGraph(String id) {
        this(id, id);
    }

    protected MapGraph(String globalID, String peerID) {
        super();

        this.globalID = globalID;
        this.id = globalID + ':' + peerID;

        if (global.put(id, this)!=null)
            throw new RuntimeException("graph " + id  + " already exists");

    }

    /** call this at the end of implementing class constructors */
    protected void init() {
        this.vertices = newVertexMap();
        this.edges = newEdgeMap();
    }

    protected abstract Map<X, Edge> newEdgeMap();

    protected abstract Map<X, Vertex> newVertexMap();


    public String newID() {
        String r;
        do {
            long low = UUID.randomUUID().getLeastSignificantBits();
            long high = UUID.randomUUID().getMostSignificantBits();
            r = new String(Base64.getEncoder().encode(
                    Bytes.concat(
                            Longs.toByteArray(low),
                            Longs.toByteArray(high)
                    )
            ));
        } while (this.vertices.containsKey(r));
        return r;
    }

    public MVertex<X> addVertex(Object id) {


        if (null == id) {
            id = newID();
        }
        else {

            //throw new RuntimeException("id must be non-null");
//            boolean done = false;
//            while (!done) {
//                idString = this.getNextId();
//                vertex = this.vertices.get(idString);
//                if (null == vertex)
//                    done = true;
//            }

            if (this.vertices.containsKey(id)) {
                throw ExceptionFactory.vertexWithIdAlreadyExists(id);
            }
        }

        MVertex<X> vertex = new MVertex<X>((X) id, this);
        this.vertices.put(vertex.id, vertex);
        return vertex;
    }

    public MVertex<X> getVertex(final Object id) {
        if (null == id)
            throw ExceptionFactory.vertexIdCanNotBeNull();

        return (MVertex<X>) this.vertices.get(id);
    }

    public MEdge<X> getEdge(final Object id) {
        if (null == id)
            throw ExceptionFactory.edgeIdCanNotBeNull();

        return (MEdge<X>) this.edges.get(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MapGraph))
            return false;
        MapGraph m = (MapGraph)obj;
        if ((vertices.size() != m.vertices.size()) ||
            (edges.size() != m.edges.size()))
                return false;

        return m.vertexSet().equals(vertexSet()) &&
                m.edgeSet().equals(edgeSet());
    }

    public Set<MVertex<X>> differentVertices(MapGraph<X> y) {
        Set<MVertex<X>> xv = vertexSet();
        Set<MVertex<X>> yv = y.vertexSet();
        return Sets.difference(xv, yv);
    }
    public Set<MEdge<X>> differentEdges(MapGraph<X> y) {
        Set<MEdge<X>> xe = edgeSet();
        Set<MEdge<X>> ye = y.edgeSet();
        return Sets.difference(xe, ye);
    }



    //TODO deepEquals which will compare the properties of each vertex and edge


    public Collection<Vertex> vertexCollection() {
        return vertices.values();
    }
    public Collection<Edge> edgeCollection() {
        return edges.values();
    }

    public Set<MVertex<X>> vertexSet() {
        return new HashSet(vertices.values());
    }
    public TreeSet<MVertex<X>> vertexSetSorted() {
        return new TreeSet(vertices.values());
    }

    public Set<MEdge<X>> edgeSet() {
        return new HashSet(edges.values());
    }
    public TreeSet<MEdge<X>> edgeSetSorted() {
        return new TreeSet(edges.values());
    }

    public Iterable<Vertex> getVertices() {
        //Unmodifiable?
        return this.vertices.values();
    }

    /*
     * Return an iterable to all the vertices in the graph that have a particular key/value property.
     * If this is not possible for the implementation, then an UnsupportedOperationException can be thrown.
     * The graph implementation should use indexing structures to make this efficient else a full vertex-filter scan is required.
     */
    @Override
    public Iterable<Vertex> getVertices(String s, Object o) {
        //TODO write a subclass with a property->vertex reverse index
        return Iterables.filter(getVertices(),
                v -> ((MVertex)v).hasProperty(s, o)
        );
    }

    public Iterable<Edge> getEdges() {
        ////Unmodifiable?
        return this.edges.values();
    }

    @Override
    public Iterable<Edge> getEdges(String s, Object o) {
        //TODO write a subclass with a property->edge reverse index
        return Iterables.filter(getEdges(),
                e -> ((MEdge)e).hasProperty(s, o)
        );
    }

    public void removeVertex(final Vertex vertex) {
        if (null == this.vertices.remove(vertex.getId()))
            throw ExceptionFactory.vertexWithIdDoesNotExist(vertex.getId());

        for (Edge edge : vertex.getEdges(Direction.BOTH)) {
            this.removeEdge(edge);
        }

        this.vertices.remove(vertex.getId());
    }

    public Edge addEdge(final Object edgeID, Object out, Object in) {
        final Vertex o = getVertex(out);
        if (o == null) throw new RuntimeException("Unknown source vertex " + out);
        final Vertex i = getVertex(in);
        if (i == null) throw new RuntimeException("Unknown target vertex " + in);

        return addEdge(edgeID, o, i, null);
    }

    public Edge addEdge(Object id, final Vertex outVertex, final Vertex inVertex, final String label) {
        /*if (label == null)
            throw ExceptionFactory.edgeLabelCanNotBeNull();*/



        if (null != id) {
            if (this.edges.containsKey(id)) {
                throw ExceptionFactory.edgeWithIdAlreadyExist(id);
            }
        } else {
            id = newID();
        }

        MEdge<X> edge = new MEdge<X>((X)id,
                (MVertex)outVertex,
                (MVertex)inVertex,
                label, this);
        this.edges.put(edge.id, edge);


        ((MVertex) outVertex).add(edge, true);
        ((MVertex) inVertex).add(edge, false);
        return edge;

    }

    public void removeEdge(final Edge edge) {

        if (null == this.edges.remove(edge.getId().toString())) {
            return;
        }


        MVertex<X> outVertex = ((MEdge) edge).outVertex;
        MVertex<X> inVertex = ((MEdge) edge).inVertex;

        Object edgeID = edge.getId();
        if (null != outVertex && null != outVertex.outEdges) {
            outVertex.remove(edgeID, false);
        }
        if (null != inVertex && null != inVertex.inEdges) {
            inVertex.remove(edgeID, true);
        }



    }

    public GraphQuery query() {
        return new DefaultGraphQuery(this);
    }


    public String toString() {
        return StringFactory.graphString(this, "vertices:" + this.vertices.size() + " edges:" + this.edges.size());
    }

    public void clear() {
        this.vertices.clear();
        this.edges.clear();
    }

    public void shutdown() {
    }

//    private String getNextId() {
//        String idString;
//        while (true) {
//            idString = this.currentId.toString();
//            this.currentId++;
//            if (null == this.vertices.get(idString) || null == this.edges.get(idString) || this.currentId == Long.MAX_VALUE)
//                break;
//        }
//        return idString;
//    }

    public Features getFeatures() {
        return FEATURES;
    }

//    protected class TinkerKeyIndex<T extends TinkerElement> extends TinkerIndex<T> implements Serializable {
//
//        private final Set<String> indexedKeys = new HashSet<String>();
//        private TinkerGraph graph;
//
//        public TinkerKeyIndex(final Class<T> indexClass, final TinkerGraph graph) {
//            super(null, indexClass);
//            this.graph = graph;
//        }
//
//        public void autoUpdate(final String key, final Object newValue, final Object oldValue, final T element) {
//            if (this.indexedKeys.contains(key)) {
//                if (oldValue != null)
//                    this.remove(key, oldValue, element);
//                this.put(key, newValue, element);
//            }
//        }
//
//        public void autoRemove(final String key, final Object oldValue, final T element) {
//            if (this.indexedKeys.contains(key)) {
//                this.remove(key, oldValue, element);
//            }
//        }
//
//        public void createKeyIndex(final String key) {
//            if (this.indexedKeys.contains(key))
//                return;
//
//            this.indexedKeys.add(key);
//
//            if (InfiniVertex.class.equals(this.indexClass)) {
//                KeyIndexableGraphHelper.reIndexElements(graph, graph.getVertices(), new HashSet<String>(Arrays.asList(key)));
//            } else {
//                KeyIndexableGraphHelper.reIndexElements(graph, graph.getEdges(), new HashSet<String>(Arrays.asList(key)));
//            }
//        }
//
//        public void dropKeyIndex(final String key) {
//            if (!this.indexedKeys.contains(key))
//                return;
//
//            this.indexedKeys.remove(key);
//            this.index.remove(key);
//
//        }
//
//        public Set<String> getIndexedKeys() {
//            if (null != this.indexedKeys)
//                return new HashSet<String>(this.indexedKeys);
//            else
//                return Collections.emptySet();
//        }
//    }

    abstract protected static class MElement<X extends Serializable> implements Element, Serializable {

        public Map<String, Serializable> properties = null;
        public X id;
        public String graphID;
        transient String globalID;
        transient MapGraph<X> graph;

        public MElement() {

        }

        protected MElement(final X id, final MapGraph<X> graph) {
            setGraph(graph.id);
            this.graph = graph;
            this.id = id;
        }

        public boolean hasProperty(String k, Object value) {
            if (properties == null) return false;
            Serializable x = properties.get(k);
            if (x == null) return false;
            return x.equals(value);
        }


        public Map<String, Serializable> prop(final boolean createIfMissing) {
            if (properties == null) {
                if  (createIfMissing) {

                    //TODO make this an abstract method of MapGraph
                    //properties = new LinkedHashMap(2);
                    properties = new ConcurrentHashMap(2);
                }
                else
                    return InfinispanCollections.emptyMap();
            }
            return properties;
        }

        public MapGraph<X> graph() {
            if (graph == null) {
                graph = MapGraph.the(graphID);
                if (graph == null)
                    throw new RuntimeException(this + " refers to unknown graph " + graphID);
            }
            return graph;
        }

        protected void setGraph(String newGraphID) {
            if (this.graphID== null || !this.graphID.equals(newGraphID)) {
                graphID = newGraphID;
                globalID = null;
                graph = null; //invalidates it, so use graph() to access this field
            }
        }

        public Set<String> getPropertyKeys() {
            return prop(false).keySet();
        }

        public <T> T getProperty(final String key) {
            return (T) prop(false).get(key);
        }

        public void setProperty(final String key, final Object value) {
            Serializable v = (Serializable)value;
            ElementHelper.validateProperty(this, key, value);
            Object oldValue = prop(true).put(key, v);
            afterAdd(key, v);
        }

        abstract protected void afterAdd(final String key, final Serializable value);

        public <T> T removeProperty(final String key) {
            Serializable oldValue = prop(false).remove(key);
            if (oldValue!=null) {
                beforeRemove(key, oldValue);
                return (T) oldValue;
            }
            return null;
        }



        public boolean isGlobal() {
            return graphID != null;
        }

        public String global() {
            if (!isGlobal()) return null;

            return globalID = graphID.substring(0, graphID.indexOf(':'));
        }

        abstract protected void beforeRemove(final String key, final Serializable oldValue);

        final static int PRIME = 31;
        final static int PRIME2 = 92821;

        public int hashCode() {
            if (graphID == null) return id.hashCode();

            return hashL(this.id.hashCode(), global().hashCode()) ;
        }


        /** also includes properties in the hash */
        public int hashCodeDeep() {
            return PRIME * hashCode() + properties.hashCode();
        }
        public final static int hashL(int a, int b) {
            return PRIME2 * (31 + a ) + b ;
        }

        public X getId() {
            return this.id;
        }

        public boolean equals(final Object object) {
            if (object.getClass()!=getClass()) return false;
            MElement o = (MElement)object;
            if (o.getId().equals(getId())) {
                if (isGlobal())
                    return o.global().equals(global());
                return true;
            }
            return false;
        }

        /** also includes properties in the equality test */
        public boolean deepEquals(final Object object) {
            if (object.getClass()!=getClass()) return false;
            MElement o = (MElement)object;
            return o.getId().equals(getId()) && o.global().equals(global()) && properties.equals(o.properties);
        }

    }

    public static <X extends Serializable> MapGraph<X> the(String graphID) {
        MapGraph<X> g = global.get(graphID);
        return g;
    }


    public static class MVertex<X extends Serializable> extends MElement<X> implements Vertex, Serializable {

        public final Map<String, Set<Edge>> outEdges = new LinkedHashMap();
        public final Map<String, Set<Edge>> inEdges = new LinkedHashMap();

        protected MVertex(final X id, final MapGraph<X> graph) {
            super(id, graph);
        }

        public Iterable<Edge> getEdges(final Direction direction, final String... labels) {
            if (direction.equals(Direction.OUT)) {
                return this.getOutEdges(labels);
            } else if (direction.equals(Direction.IN))
                return this.getInEdges(labels);
            else {
                return Iterables.concat(this.getInEdges(labels), this.getOutEdges(labels));
            }
        }



        public Iterable<Vertex> getVertices(final Direction direction, final String... labels) {
            return new VerticesFromEdgesIterable(this, direction, labels);
        }

        public Iterable<Edge> getEdges(final Map<String, Set<Edge>> e, final String... labels) {
            if (labels.length == 0) {
                return Iterables.concat(e.values());
            } else if (labels.length == 1) {
                final Set<Edge> edges = e.get(labels[0]);
                if (null == edges) {
                    return Collections.emptyList();
                } else {
                    return edges;
                }
            } else {
                final Set<String> labelSet = Sets.newHashSet(labels);
                return Iterables.concat(Iterables.transform(e.entrySet(), x -> {
                    if (labelSet.contains(x.getKey()))
                        return x.getValue();
                    return Collections.emptyList();
                }));
            }
        }


        private Iterable<Edge> getInEdges(final String... labels) {
            return getEdges(inEdges, labels);
        }
        private Iterable<Edge> getOutEdges(final String... labels) {
            return getEdges(outEdges, labels);
        }

        public VertexQuery query() {
            return new DefaultVertexQuery(this);
        }

        public String toString() {
            if (isGlobal())
                return new StringBuilder().append("v[").append(global()).append(':').append(getId()).append("]").toString();
            else
                return new StringBuilder().append("v[").append(getId()).append("]").toString();

        }

        public Edge addEdge(final String label, final Vertex vertex) {
            return graph().addEdge(null, this, vertex, label);
        }




        private Set<Edge> newEdgeSet(int size) {
            return new LinkedHashSet<Edge>(size);
        }



        @Override
        protected void afterAdd(String key, Serializable value) {
            //this.graph.vertexKeyIndex.autoUpdate(key, value, oldValue, (InfiniVertex) this);
            graph().update(this);
        }

        @Override
        protected void beforeRemove(String key, Serializable oldValue) {
               //this.graph.vertexKeyIndex.autoRemove(key, oldValue, (InfiniVertex) this);
        }


        @Override
        public void remove() {
            graph().removeVertex(this);
        }


        public boolean remove(Object edgeID, boolean incoming) {
            Map<String, Set<Edge>> target = incoming ? outEdges : inEdges;
            if (target.remove(edgeID)!=null) {
                graph().update(this);
                return true;
            }

            return false;
        }

        public boolean add(MEdge<X> edge, boolean incoming) {
            Map<String, Set<Edge>> target = incoming ? outEdges : inEdges;

            String label = edge.getLabel();
            Set<Edge> edges = target.get(label);
            if (null == edges) {
                target.put(label, edges = newEdgeSet(1));
            }
            if (edges.add(edge)) {
                graph().update(this);
                return true;
            }

            return false;
        }
    }


    /** called if an vertex changes its properties */
    protected void update(MVertex<X> xmVertex) {

    }

    /** called if an edge changes its properties */
    protected void update(MEdge<X> xmVertex) {

    }

    public static class MEdge<X extends Serializable> extends MElement<X> implements Edge, Externalizable {

        protected String label;
        protected MVertex<X> inVertex;
        protected MVertex<X> outVertex;

        public MEdge( ) {

        }

        protected MEdge(final X id, final MVertex<X> outVertex, final MVertex<X> inVertex, final String label, final MapGraph<X> graph) {
            super(id, graph);
            this.label = label;
            this.outVertex = outVertex;
            this.inVertex = inVertex;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(id);
            out.writeUTF(graphID);
            out.writeUTF(label == null ? "" : label);
            out.writeObject(outVertex.getId());
            out.writeObject(inVertex.getId());
            out.writeObject(prop(false));
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            id = (X) in.readObject();

            setGraph(in.readUTF());

            label = in.readUTF();
            outVertex = graph().getVertex( in.readObject() );
            inVertex = graph().getVertex( in.readObject() );
            properties = (Map<String, Serializable>) in.readObject();
        }



        public String getLabel() {
            return this.label;
        }

        public Vertex getVertex(final Direction direction) throws IllegalArgumentException {
            if (direction.equals(Direction.IN))
                return this.inVertex;
            else if (direction.equals(Direction.OUT))
                return this.outVertex;
            else
                throw ExceptionFactory.bothIsNotSupported();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("e[").append(global()).append(':').append(getId()).append("][").append(getVertex(Direction.OUT).getId()).append("-");
            String label = getLabel();
            if (label!=null)
                    sb.append(getLabel());
            return sb.append("->").append(getVertex(Direction.IN).getId()).append(']').toString();
        }

        @Override
        protected void afterAdd(String key, Serializable value) {
            //this.graph.edgeKeyIndex.autoUpdate(key, value, oldValue, (InfiniEdge) this);*/
            graph().update(this);
        }

        @Override
        protected void beforeRemove(String key, Serializable oldValue) {
            //this.graph.edgeKeyIndex.autoRemove(key, oldValue, (InfiniEdge) this);*/
            graph().update(this);
        }

        @Override
        public void remove() {
            graph().removeEdge(this);
        }



//        @Override
//        public boolean equals(Object object) {
//            return label.equals(((MEdge)object).label);
//        }
    }
}