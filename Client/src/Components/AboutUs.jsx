import React from 'react';
import './AboutUs.css'; 

function AboutUs() {
  return (
    <div className="about-us-container">
      <div className="about-us-box">
        <p className="section-header">Mission</p>
        <div className="mission-container">
          <img src="/images/1.jpg" alt="People standing together" className="People" />
          <img src="/images/2.jpg" alt="Girl blowing leaf" className="leaf" />
          <img src="/images/3.jpg" alt="Guy with a bag" className="Guy-with-a-bag" />
          <img src="/images/4.jpg" alt="Guy with car" className="car" />
        </div>

        <p className="section-header">Our Mission</p>
          <p>To develop a comprehensive program of resources, which immediately relieves the burden of food insecurity* for UMBC members and connects them to ongoing support networks, in order to enhance their academic retention and career success.</p>
          <p><br /><em>*The U.S. Department of Agriculture (USDA) defines food insecurity as a lack of consistent access to enough food for an active, healthy life.</em>
        </p>

        <div className="image-container">
          <img src="/images/5.jpeg" alt="Food Insecurity" className="food" />
        </div>

        <p className="section-header">Our Vision</p>
        <p>
          To relieve the burden of food insecurity and related issues for all UMBC members and provide the UMBC community with ongoing support by connecting them with on-campus resources, such as those within the Division of Student Affairs’s Retriever Support Services and off-campus resources, such as the Supplemental Nutrition Assistance Program (SNAP).
        </p>

        <p className="section-header">Our Values</p>
        <ul className="values-list">
          <li>We know that having inadequate food resources is a normal part of society, and we believe that nobody should have to sacrifice basic needs, whether material or psychosocial, in order to get through college.</li>
          <li>We are a community that operates on the premise that everyone has something valuable to contribute to our organization; we exist in solidarity and reciprocity with UMBC members experiencing food insecurity.</li>
          <li>We are committed to learning from our community by encouraging campus-wide participation and incorporating feedback loops and adaptability into our operations.</li>
          <li>We do not gatekeep. We recognize that financial decision-making is complex, and therefore we do not require proof of need to access our pantry resources.</li>
        </ul>

        <div className="image-container">
          <img src="/images/6.png" alt="Symbol" className="sym" />
        </div>
        <p>Our name and logo represent our commitment to provide the UMBC members with the essentials (food and personal care items) that they need to succeed. As an organization, Retriever Essentials exists through the support of UMBC students, faculty, and staff members, thus, True Grit’s paw encircles our “essentials”.</p>
      </div>
    </div>
  );
}

export default AboutUs;


