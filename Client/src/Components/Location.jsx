function Location() {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <div style={{ backgroundColor: 'white', color: 'black', padding: '40px', borderRadius: '10px', textAlign: 'center', maxWidth: '600px', width: '90%' }}>
          <h1 style={{ marginBottom: '10px' }}>Location</h1>
          <p style={{ fontSize: '18px', marginBottom: '20px' }}>Commons 1A10, To see this zoom in and look near the quad, easiest to see in satellite imagery mode.  </p>
          <iframe
            title="UMBC Commons Location"
            src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3105.010676389317!2d-76.71328452384814!3d39.25567827144626!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x89c81d2c6b168f0b%3A0x8e3f4b7f2b3e3431!2sThe%20Commons%2C%20UMBC!5e0!3m2!1sen!2sus!4v1710460600000"
            width="100%"
            height="350"
            style={{ border: '0', borderRadius: '10px' }}
            allowFullScreen=""
            loading="lazy"
          ></iframe>
        </div>
      </div>
    );
  }
  
  export default Location;
  
  