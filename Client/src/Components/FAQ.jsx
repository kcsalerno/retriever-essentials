import React, { useState } from 'react';
import './faq.css';

function FAQ() {
  const [openIndex, setOpenIndex] = useState(null);

  const toggleAnswer = (index) => {
    setOpenIndex(openIndex === index ? null : index);
  };

  const faqs = [
    {
      question: "What are your store hours?",
      answer: "Our hours of operation are Monday to Friday, 11 AM to 7 PM. We are closed on weekends."
    },
    {
      question: "Where are you located?",
      answer: "We are located at The Commons 1A10, UMBC campus."
    },
    {
      question: "How does this work?",
      answer: (
        <div>
          <p>Here’s a quick tutorial on how to use our app:</p>
          <div className="video-wrapper">
            <iframe
              width="100%"
              height="315"
              src="https://www.youtube.com/embed/4JmdALKwB58"
              title="Tutorial Video"
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            ></iframe>
          </div>
        </div>
      )
    }
    
    
  ];

  return (
    <div className="faq-container">
      <div className="faq-box">
        <h1>FAQ</h1>
        {faqs.map((faq, index) => (
          <div 
            key={index} 
            className={`faq-item ${openIndex === index ? 'open' : ''}`} 
            onClick={() => toggleAnswer(index)}
          >
            <h3>
              {faq.question}
              <span style={{ marginLeft: '10px' }}>
                {openIndex === index ? '-' : '+'}
              </span>
            </h3>
            <p>{faq.answer}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default FAQ;

