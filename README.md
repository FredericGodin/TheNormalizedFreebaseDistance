This code is the basic implementation of the Normalized Freebase Distance, presented at the Extended Semantic Web Conference 2014. 

The paper can be found at: http://2014.eswc-conferences.org/sites/default/files/eswc2014pd_submission_73.pdf

The poster can be found at: http://www.slideshare.net/fgodin/the-normalized-freebase-distance-nfd

To be able to run the code, you need a Freebase SPARQL endpoint.
If you want to set it up yourself, you can download the RDF triples here: https://developers.google.com/freebase/data
You can use Virtuoso to query the triples.
There is currently no public SPARQL endpoint for Freebase. 
Freebase used the MQL language for querying.

If you use the code, please cite us!

@inproceedings{godinetal_nfd,
    abstract = {{In this paper, we propose the Normalized Freebase Distance (NFD), a semantic-aware distance metric that is based on similar principles as the Normalized Web Distance (NWD). We illustrate that the NFD has clear advantages when comparing ambiguous concepts, while remaining computationally efficient.}},
    author = {Godin, Fr\'{e}deric and De Nies, Tom and Beecks, Christian and De Vocht, Laurens and De Neve, Wesley and Mannens, Erik and Seidl, Thomas and Van de Walle, Rik},
    day = {25-29},
    journal = {Proceedings of the Extended Semantic Web Conference 2014},
    keywords = {distance, freebase, nfd, normalized},
    location = {Crete, Greece},
    month = may,
    title = {{The Normalized Freebase Distance}},
    year = {2014}
}
